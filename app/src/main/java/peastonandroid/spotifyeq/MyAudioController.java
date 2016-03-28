package peastonandroid.spotifyeq;

import com.spotify.sdk.android.player.AudioController;

/**
 * Created by Phillip on 3/10/2016.
 */
public class MyAudioController implements AudioController {
    //This controller will implement the superpowered player rather than the spotify player.
    @Override
    public int onAudioDataDelivered(short[] frames, int numFrames, int sampleRate, int channels){
        //THIS is where you send the data to the other player.
        return 1;
    }

    public void onAudioFlush(){
        //Zero your buffers!!
    }

    public void onAudioPaused(){
        //this has to pause the player in superpowered.
    }

    public void onAudioResumed(){
        //Keep track of the playhead if you can.
    }

    public void start(){
        //Load the buffer, might need to look up how to start. maybe initialize?
    }

    public void stop(){
        //don't know what this actually does, maybe cleanup?
    }


}
