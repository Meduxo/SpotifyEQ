//package peastonandroid.spotifyeq;
//
//import android.util.Log;
//
//import com.spotify.sdk.android.player.AudioController;
//import com.spotify.sdk.android.player.AudioRingBuffer;
//
///**
// * Created by Phillip on 3/10/2016.
// * Since this will be blocking read/write, make sure this is written to an AudioRingBuffer, whose size can be chosen with the return
// * of onAudioDataDelivered.
// */
//
//
//public class MyAudioController implements AudioController {
//    public void setNumFrames(int numFrames){
//        mNumFrames = numFrames;
//    }
//    public int getNumFrames(){
//        return mNumFrames;
//    }
//    public void setSampleRate(int sampleRate){
//        mSampleRate = sampleRate;
//    }
//    public int getSampleRate(){
//        return mSampleRate;
//    }
//    public void setChannels(int channels){
//        mChannels = channels;
//    }
//    public int getChannels(){
//        return mSampleRate;
//    }
//
//    public
//    private BiQuadraticFilter mBiquad_1;
//    private BiQuadraticFilter mBiquad_2;
//    private BiQuadraticFilter mBiquad_3;
//    private short[] tempBuffer;
//    private AudioRingBuffer mAudioRingBuffer;
//    private double[] mBufferL;
//    private double[] mBufferR;
//    private double[] mBuffer;
//    private int mNumFrames;
//    private int mSampleRate;
//    private int mChannels;
//
//    private Runnable mRunnable;
//    private Thread mThread;
//
//    public double cFreq1, cFreq2, cFreq3, Q1, Q2, Q3; //set these in your MainActivity, control with link to sliders.
//
//    public MyAudioController(short[] outBuffer, double Freq1, double q1, double Freq2, double q2, double Freq3, double q3) {
//
//
//        cFreq1 = Freq1;
//        cFreq2 = Freq2;
//        cFreq3 = Freq3;
//        Q1=q1;Q2=q2;Q3=q3;
//
//    }
//
//
//
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//    mAudioRingBuffer = new AudioRingBuffer(numFrames);//process in here, peek and write.
//    tempBuffer = new short[numFrames];
//    mAudioRingBuffer.clear();
//    mBiquad_1 = new BiQuadraticFilter(BiQuadraticFilter.Type.LOWPASS, cFreq1, sampleRate, Q1);
//    mBiquad_2 = new BiQuadraticFilter(BiQuadraticFilter.Type.HIGHPASS, cFreq2, sampleRate, Q2);
//    mBiquad_3 = new BiQuadraticFilter(BiQuadraticFilter.Type.PEAK, cFreq3, sampleRate, Q3);
//    mThread.interrupt();
//    //set centerfreq
//
//
//    //
//
//    switch (channels) {
//        case 1: {
//            mBuffer = new double[numFrames];
//            for (int ind = 0; ind < numFrames; ind++) {
//                //Peak is last, ends are first.
//                mBuffer[ind] = mBiquad_3.filter(
//                        mBiquad_2.filter(
//                                mBiquad_1.filter((double)frames[ ind ]
//                                )
//                        )
//                );
//                tempBuffer[ind] = (short) mBuffer[ind];
//
//
//            }
//            mAudioRingBuffer.write(tempBuffer);
//        }
//        break;
//
//        case 2: {
//            mBufferL = new double[numFrames/2];
//            mBufferR = new double[numFrames/2];
//            for (int ind = 1; ind <= numFrames; ind+=2) {
//                mBufferL[ind-1] = mBiquad_3.filter(mBiquad_2.filter(mBiquad_1.filter((double)frames[ ind-1 ])));
//                mBufferR[ind] = mBiquad_3.filter(mBiquad_2.filter(mBiquad_1.filter((double)frames[ ind ])));
//                tempBuffer[ind-1] = (short) mBufferL[ind-1];
//                tempBuffer[ind] = (short) mBufferR[ind];
//            }
//            mAudioRingBuffer.write(tempBuffer);
//        }
//        break;
//
//    }
//
//
//    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//
//
//    @Override
//    public int onAudioDataDelivered(short[] frames, int numFrames, int sampleRate, int channels) {
//        //Called whenever Player receives audio data.
//        //This method is synchronous and therefore blocking. Any long running operations will affect playback quality.
//        mAudioRingBuffer = new AudioRingBuffer(numFrames);
//        mAudioRingBuffer.write(frames);
//
//        return numFrames;
//    }
//
//    @Override
//    public void onAudioFlush(){
//
//        //Zero your buffers!!
//    }
//
//    @Override
//    public void onAudioPaused(){
//        //this has to pause the player in superpowered.
//    }
//
//    @Override
//    public void onAudioResumed(){
//        //Keep track of the playhead if you can.
//    }
//
//    @Override
//        public void start() {
//            Log.d(PLAYER_TAG, "start");
//        }
//    }
//
//    @Override
//    public void stop() {
//        Log.d(PLAYER_TAG,"stop");
//    }
//
//
//}
