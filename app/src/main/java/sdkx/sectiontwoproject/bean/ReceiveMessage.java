package sdkx.sectiontwoproject.bean;

/**
 * webSocket格式
 */
public class ReceiveMessage {

    //发送人
    private String sendName;

    //接收人
    private String receive;

    //信息类别码
    private String code;

    //数据
    private DataBean data;

    public ReceiveMessage() {
    }

    public ReceiveMessage(String sendName, String receive, String code, DataBean data) {
        this.sendName = sendName;
        this.receive = receive;
        this.code = code;
        this.data = data;
    }

    public String getSendName() {
        return sendName;
    }

    public void setSendName(String sendName) {
        this.sendName = sendName;
    }

    public String getReceive() {
        return receive;
    }

    public void setReceive(String receive) {
        this.receive = receive;
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
    public static class DataBean {
        private String state;
        private String reason;

        public String getState() {
            return state;
        }

        public void setState(String state) {
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



