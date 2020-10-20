public class JNIAdapter {
    private long nClassPointer = 0;
    public JNIAdapter()
    {
        nClassPointer = callConstructor();
    }

    public void destructor()
    {
        callDestructor(nClassPointer);
    }

    public long getCurProcID()
    {
        return getCurProcID(nClassPointer);
    }

    public String getCurProcName()
    {
        return getCurProcName(nClassPointer);
    }

    public int getCurProcThreadCnt()
    {
        return getCurProcThreadCnt(nClassPointer);
    }

    public String getProgramNameByActiveWindow()
    {
        return getProgramNameByActiveWindow(nClassPointer);
    }

    public double getCpuLoadByProcess()
    {
        return getCpuLoadByProcess(nClassPointer);
    }

    public long getRAMLoadByProcess()
    {
        return getRAMLoadByProcess(nClassPointer);
    }

    public boolean toNextProcess()
    {
        return toNextProcess(nClassPointer);
    }

    public boolean updateSnap()
    {
        return updateSnap(nClassPointer);
    }

    private native long callConstructor();
    private native void callDestructor(long nClassPointer);
    private native long getCurProcID(long nClassPointer);
    private native String getCurProcName(long nClassPointer);
    private native int getCurProcThreadCnt(long nClassPointer);
    private native String getProgramNameByActiveWindow(long nClassPointer);
    private native double getCpuLoadByProcess(long nClassPointer);
    private native long getRAMLoadByProcess(long nClassPointer);
    private native boolean toNextProcess(long nClassPointer);
    private native boolean updateSnap(long nClassPointer);
}
