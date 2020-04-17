package sdkx.sectiontwoproject.bean;

/**
 * webSocket格式
 */
public class ReceiveMessage {


    /**
     * code : html
     * data : {"state":1,"reason":"出界考试不合格"}
     * receive : 123
     * sendName : 1234
     */

    private String code;
    private DataBean data;
    private String receive;
    private String sendName;

    public ReceiveMessage(String code, DataBean data, String receive, String sendName) {
        this.code = code;
        this.data = data;
        this.receive = receive;
        this.sendName = sendName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getReceive() {
        return receive;
    }

    public void setReceive(String receive) {
        this.receive = receive;
    }

    public String getSendName() {
        return sendName;
    }

    public void setSendName(String sendName) {
        this.sendName = sendName;
    }

    public static class DataBean {
        /**
         * state : 1  0
         * reason : 出界考试不合格
         */

        private int state;
        private String reason;

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }
}



