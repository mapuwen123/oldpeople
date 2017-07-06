package com.aiminerva.oldpeople.deviceservice;


/**
 * BlueTooth Device Protocal ����
 */
public abstract class PrtBase {

    protected PrtBaseListener mListener = null;

    public final static byte CMD_BLOODOXYGENVALUE = 0x04;//血氧 801
    public final static byte CMD_ECGVALUE = 0x05;//心电 201 EGC
    public final static byte CMD_BLOODPRESSVALUE = 0x01;//血压 503
    public final static byte CMD_BODYFATVALUE = 0x06;//体质   301
    public final static byte CMD_BLOODSUGERVALUE = 0x02;//血糖 601
    public final static byte CMD_TEMPVALUE = 0x03;//体温 ？

    private String mUUID = "";

    public String getmUUID() {
        return mUUID;
    }

    public interface PrtBaseListener {
        public boolean onMasterSend(byte[] buf);

        public void onMasterErr();
//    	public boolean onMasterReceive( byte command, PrtData result );
    }

    /**
     * protocal response data class
     *
     * @author Administrator
     */
    public class PrtData extends Object {
    }

    protected PrtBase(String uuid) {
        this.mUUID = uuid;
    }

    public synchronized void setListener(PrtBaseListener listener) {
        mListener = listener;
    }

    public synchronized void removeListener() {
        mListener = null;
    }

    public void init() {

    }

    public void uinit() {

    }
}
