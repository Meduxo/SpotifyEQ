package peastonandroid.spotifyeq;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import peastonandroid.spotifyeq.AudioRingBuffer;
import com.spotify.sdk.android.player.AudioController;
import com.spotify.sdk.android.player.Player;

import kaaes.spotify.webapi.android.SpotifyApi;

/**
 * Created by Phillip on 3/10/2016.
 * Since this will be blocking read/write, make sure this is written to an AudioRingBuffer, whose size can be chosen with the return
 * of onAudioDataDelivered.
 * array goes into circular buffer
 * feed your filter function from circular buffer on full buffer(peek for loading data sample by sample?
 * pass to playback using audioTrack
 *
 */


public class MyAudioController implements AudioController {
    private Runnable mRunnable;

    private Thread mThread;
    private short[] tempBuffer;
    private double[] mBufferL;
    private double[] mBufferR;
    //private double[] mBuffer;
    private BiQuadraticFilter mBiquad_1;
    private BiQuadraticFilter mBiquad_2;
    private BiQuadraticFilter mBiquad_3;



    private double cFreq1, cFreq2, cFreq3, Q1, Q2, Q3, mGainDB;
    private int intSize;
    private int tempChannelState;
    private int mChannels;
    private int mSampleRate;
    private int runningTotalFloatOffset=0;
    private int mNumFrames;
    private int mBufferSize; //buffer size for ringBuffer. Determine your max with this.
    private int mBufferMax; //numFrames/mBufferIndex... this will be how many total laps the ringbuffer takes.
    private int mBufferLapCount=0; //This will be the current lap number. use for written callback




    private AudioTrack mTrack;

    private AudioRingBuffer mAudioRingBuffer, mAudioRingBufferL, mAudioRingBufferR;
    final String PLAYER_TAG = "SpotifyPlayer";
    @Override
    public void start() { //initialized on player creation

        //mThread = new Thread(mRunnable);
        //mThread.start();
        Log.d(PLAYER_TAG, "start");
    }

    @Override
    public void stop() { //stop thread on destruction
        /*try {
            mRunnable.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        Log.d(PLAYER_TAG, "stop");
    }

    @Override
    public int onAudioDataDelivered(short[] frame, int numFrames, int sampleRate, int channels) {
        int written = 0;

        //if (mTrack == null) {
        intSize = numFrames;
        mBufferSize = 16384;
        short[] frames = new short[mBufferSize];
        mAudioRingBuffer = new AudioRingBuffer(mBufferSize);



        mChannels = channels;
        mNumFrames = frames.length;

        mSampleRate = sampleRate;

        mBiquad_1 = new BiQuadraticFilter(BiQuadraticFilter.Type.HIGHPASS, cFreq1, sampleRate, Q1);
        mBiquad_2 = new BiQuadraticFilter(BiQuadraticFilter.Type.PEAK, cFreq2, sampleRate, Q2, mGainDB);
        mBiquad_3 = new BiQuadraticFilter(BiQuadraticFilter.Type.LOWPASS, cFreq3, sampleRate, Q3);
        //interleave if necessary, determine mBufferSize with channels.

        if (mChannels == 1) {

            tempChannelState = AudioFormat.CHANNEL_OUT_MONO;
            this.setTrack();

                    /*2 * android.media.AudioTrack.getMinBufferSize(
                    sampleRate,
                    ,
                    AudioFormat.ENCODING_PCM_16BIT);*/
            tempBuffer = new short[intSize];


            //mAudioRingBufferL.write(frames, intSize); //copy input but don't return yet. STAGE 1
            //mAudioRingBufferL.peek(tempBuffer); //Fill in this buffer, return count. STAGE 2

            //Filter Loop Mono
            for (int ind = 0; ind < intSize; ind++) {
                //which buffer count are we in? what lap?

                tempBuffer[ind] = (short)
                        (mBiquad_3.filter(
                                mBiquad_2.filter(
                                        mBiquad_1.filter(
                                                (double) (frames[ind]/32767f)
                                        )
                                )
                        )*32767)
                ;              //(data we write to, position to start, how long to write for)


                //runningTotalFloatOffset+=numFrames;

            }
            written = mAudioRingBuffer.write(tempBuffer, numFrames);

            //mTrack.play();



        }


        else if (mChannels == 2) {
            intSize = numFrames;
            tempChannelState = AudioFormat.CHANNEL_OUT_STEREO;
            this.setTrack();

                    /*2 * android.media.AudioTrack.getMinBufferSize(
                    sampleRate,
                    ,
                    AudioFormat.ENCODING_PCM_16BIT);*/

            //mAudioRingBufferL = new AudioRingBuffer(mBufferSize);
            //mAudioRingBufferR = new AudioRingBuffer(mBufferSize);
            mBufferL = new double[numFrames];
            mBufferR = new double[numFrames];
            tempBuffer = new short[numFrames];
            mAudioRingBuffer.write(frame, mBufferSize);
            mAudioRingBuffer.peek(frames);
            for (int ind = 0; ind < mBufferSize-1; ind += 2) {
                //Deinterleave

                mBufferL[ind] = (double) (frames[ind]/32767.0f);
                mBufferR[ind] = (double) (frames[ind+1]/32767.0f);

                mBufferL[ind] =
                        //mBiquad_3.filter(
                        //mBiquad_2.filter(
                        //mBiquad_1.filter(
                        mBufferL[ind]
                        //)))
                ;
                String Text = String.format("%d ", frames[ind]);

                Log.d("Float_data", Text);
                mBufferR[ind] =
                        //mBiquad_3.filter(
                        //mBiquad_2.filter(
                        //mBiquad_1.filter(
                        mBufferR[ind]
                        //)))
                ;
                //INTERLEAVE
                tempBuffer[ind] = (short) (mBufferL[ind]*32767);
                tempBuffer[ind+1] = (short) (mBufferR[ind]*32767);
                String Text2 = String.format("%d ", tempBuffer[ind]);
                Log.d("AfterFilter", Text2);
            }

            //mTrack.write
            mTrack.play();
            written = mTrack.write(tempBuffer, 0, numFrames);

            //runningTotalFloatOffset+=numFrames;


        }



        //mTrack = new AudioTrack()

                           // mTrack.play();//i don't know if you have to play in here =(
        //}

        //this is your float data, sending return, Roger.

        Log.d(PLAYER_TAG, "numFrames " + numFrames + " frames length " + numFrames + " written " + written + " sampleRate " + sampleRate + " channels " + channels);
        return written;
    }

    public void setTrack()
    {
    mTrack = new AudioTrack(
            AudioManager.STREAM_MUSIC,
            mSampleRate,
            tempChannelState,
            AudioFormat.ENCODING_PCM_16BIT,
            intSize,
            AudioTrack.MODE_STREAM);
    }
    public AudioTrack getTrack()
    {
        return mTrack;
    }
    public double getcFreq1() {
        return cFreq1;
    }

    public void setcFreq1(double cFreq1) {
        this.cFreq1 = cFreq1;
    }

    public double getcFreq2() {
        return cFreq2;
    }

    public void setcFreq2(double cFreq2) {
        this.cFreq2 = cFreq2;
    }

    public double getcFreq3() {
        return cFreq3;
    }

    public void setcFreq3(double cFreq3) {
        this.cFreq3 = cFreq3;
    }

    public double getQ1() {
        return Q1;
    }

    public void setQ1(double q1) {
        Q1 = q1;
    }

    public double getQ2() {
        return Q2;
    }

    public void setQ2(double q2) {
        Q2 = q2;
    }

    public double getQ3() {
        return Q3;
    }

    public void setQ3(double q3) {
        Q3 = q3;
    }

    public double getGainDB() {
        return mGainDB;
    }

    public void setGainDB(double gainDB) {
        mGainDB = gainDB;
    }
    @Override
    public void onAudioFlush() {
        Log.d(PLAYER_TAG, "onAudioFlush");
        if (mTrack != null)
            mTrack.flush();
    }

    @Override
    public void onAudioPaused() {
        Log.d(PLAYER_TAG, "onAudioPaused");
        mTrack.pause();
    }

    @Override
    public void onAudioResumed() {
        Log.d(PLAYER_TAG, "onAudioResumed");
        mTrack.play();
    }
}



