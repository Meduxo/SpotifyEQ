package peastonandroid.spotifyeq;

/**
 * Created by Phillip on 4/19/2016.
 */
public class AudioRingBuffer {
    private final int mCapacity;
    private final short[] mData;
    private int mSize; //keeps track of total size through new buffer writes
    private int mReadPosition;
    private int mWritePosition;

    //Spec this buffer to get the size, be sure to call clear when you make.
    public AudioRingBuffer(int capacity) {
        this.mCapacity = capacity;
        this.mData = new short[capacity];
    }

    //This write function will return the amount of samples written to the buffer.
    public int write(short[] data) {
        return this.write(data, data.length);
    }

    public synchronized int write(short[] data, int itemsCount) {
        itemsCount = Math.min(itemsCount, data.length);
        if(this.mSize + itemsCount > this.mCapacity) {
            return 0;
        } else {
            if(this.mWritePosition + itemsCount > this.mCapacity) {
                int offset = this.mCapacity - this.mWritePosition;
                System.arraycopy(data, 0, this.mData, this.mWritePosition, offset); //
                System.arraycopy(data, offset, this.mData, 0, itemsCount - offset);
            } else {
                System.arraycopy(data, 0, this.mData, this.mWritePosition, itemsCount);
            }

            this.mWritePosition = (this.mWritePosition + itemsCount) % this.mCapacity; // -> new mWritePosition is circular
            this.mSize += itemsCount; //How big is the written buffer so far?
            return itemsCount;
        }
    }

    public synchronized int peek(short[] outArray) {
        if(this.mSize == 0) {
            return 0;
        } else {
            int itemsCount = Math.min(this.mSize, outArray.length);
            if(this.mReadPosition + itemsCount > this.mCapacity) {
                int offset = this.mCapacity - this.mReadPosition;
                System.arraycopy(this.mData, this.mReadPosition, outArray, 0, offset);
                System.arraycopy(this.mData, 0, outArray, offset, itemsCount - offset);
            } else {
                System.arraycopy(this.mData, this.mReadPosition, outArray, 0, itemsCount);
            }

            return itemsCount;
        }
    }

    public synchronized void remove(int itemCount) {
        if(itemCount > 0) {
            itemCount = Math.min(itemCount, this.mSize); //which is smaller, item count or mSize?
            this.mReadPosition = (this.mReadPosition + itemCount) % this.mCapacity;
            this.mSize -= itemCount;
        }
    }

    public synchronized void clear() {
        this.mReadPosition = 0;
        this.mWritePosition = 0;
        this.mSize = 0;
    }

    synchronized int capacity() {
        return this.mCapacity;
    }

    synchronized int size() {
        return this.mSize;
    }

    synchronized int getReadPosition() {
        return this.mReadPosition;
    }

    synchronized int getWritePosition() {
        return this.mWritePosition;
    }
}

