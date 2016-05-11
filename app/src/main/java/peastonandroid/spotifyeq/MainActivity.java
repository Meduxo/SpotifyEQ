package peastonandroid.spotifyeq;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.session.MediaController;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.AudioController;
import com.spotify.sdk.android.player.AudioRingBuffer;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;

import kaaes.spotify.webapi.android.SpotifyApi;


public class MainActivity extends Activity implements
        PlayerNotificationCallback, ConnectionStateCallback {

    private ImageButton menuButton, nextButton, playButton,pauseButton, previousButton;
    private PopupWindow popupWindow;
    private boolean isPaused;
    private short[] tempBuffer;
    private double[] mBufferL;
    private double[] mBufferR;
    private double[] mBuffer;
    private BiQuadraticFilter mBiquad_1;
    private BiQuadraticFilter mBiquad_2;
    private BiQuadraticFilter mBiquad_3;
    //public double cFreq1, cFreq2, cFreq3, Q1, Q2, Q3, mGainDB;
    private int tempChannelState;
    private int mChannels;
    private int mNumFrames;
    private int mBufferSize; //buffer size for ringBuffer. Determine your max with this.
    private int mBufferMax; //numFrames/mBufferIndex... this will be how many total laps the ringbuffer takes.
    private int mBufferLapCount=0; //This will be the current lap number. use for written callback


    private LayoutInflater layoutInflater;
    private RelativeLayout relativeLayout;
    private static final String CLIENT_ID = "ea3479e0f2464105bdc683bcdd872af2";
    private static final String REDIRECT_URI = "my-first-android-eq://callback";
    SpotifyApi api = new SpotifyApi();
    private AudioTrack mTrack;
    public Player mPlayer;
    private AudioRingBuffer mAudioRingBufferL, mAudioRingBufferR;




    // Request code that will be used to verify if the result comes from correct activity
// Can be any integer
    private static final int REQUEST_CODE = 1337;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient mClient;

    public MainActivity() {}



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        menuButton = (ImageButton) findViewById(R.id.MenuButton);
        nextButton = (ImageButton) findViewById(R.id.Next);
        previousButton = (ImageButton) findViewById(R.id.Previous);
        playButton = (ImageButton) findViewById(R.id.Play);
        pauseButton = (ImageButton) findViewById(R.id.Pause);
        isPaused = false;
        relativeLayout = (RelativeLayout) findViewById(R.id.relative);

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.menu_main, null);
                popupWindow = new PopupWindow(container, 250, 1280, true);
                popupWindow.showAtLocation(relativeLayout, Gravity.NO_GRAVITY, 0, 40);

                container.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        popupWindow.dismiss();
                        return true;
                    }
                });
            }
        });


        //Authenitcate Application via Client ID.
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {



                final Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                final String PLAYER_TAG = "SpotifyPlayer";
                final MyAudioController mAudioController= new MyAudioController();
                Player.Builder b= new Player.Builder(playerConfig); //Legitimize Player
                b.setAudioController(mAudioController);
                Spotify.getPlayer(b, this, new Player.InitializationObserver() {
                    //Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
                    @Override
                    public void onInitialized(Player player) {
                        mPlayer = player;
                        //mTrack = new AudioTrack();
                        mAudioController.setcFreq1(200.0f);
                        mAudioController.setcFreq2(5000.0f);
                        mAudioController.setcFreq3(14000.0f);
                        mAudioController.setQ1(1.0f);
                        mAudioController.setQ2(1.0f);
                        mAudioController.setQ3(1.0f);
                        mAudioController.setGainDB(-50.0f);


                        mPlayer.addConnectionStateCallback(MainActivity.this);
                        mPlayer.addPlayerNotificationCallback(MainActivity.this);


                        playButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Need some logic for mPlayer.resume when it isn't the beginning of track.
                                if (!isPaused) {
                                    mPlayer.play("spotify:track:6VyJOMQgXsVut0Pnjaoefm"); //eventually this will be a search
                                    //mPlayer.play();


                                } else {
                                    mPlayer.resume();
                                    isPaused = false;
                                }
                                playButton.performHapticFeedback(1);
                                playButton.setVisibility(View.INVISIBLE);
                                playButton.setEnabled(false);
                                pauseButton.setEnabled(true);
                                pauseButton.setVisibility(View.VISIBLE);
                            }

                        });
                        pauseButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mPlayer.pause();
                                pauseButton.performHapticFeedback(1);
                                pauseButton.setVisibility(View.INVISIBLE);
                                pauseButton.setEnabled(false);
                                playButton.setEnabled(true);
                                playButton.setVisibility(View.VISIBLE);
                                isPaused = true;
                            }
                        });
                        //also need filter slider control on touch functions, may need to do a call to here in another function?
                    }


                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
        }
    }


    @Override
    public void onLoggedIn() {
        Log.d("MainActivity", "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Throwable error) {
        Log.d("MainActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Received connection message: " + message);
    }

    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
        Log.d("MainActivity", "Playback event received: " + eventType.name());
    }

    @Override
    public void onPlaybackError(ErrorType errorType, String errorDetails) {
        Log.d("MainActivity", "Playback error received: " + errorType.name());
    }

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mClient.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://peastonandroid.spotifyeq/http/host/path")
        );
        AppIndex.AppIndexApi.start(mClient, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://peastonandroid.spotifyeq/http/host/path")
        );
        AppIndex.AppIndexApi.end(mClient, viewAction);
        mClient.disconnect();
    }
}