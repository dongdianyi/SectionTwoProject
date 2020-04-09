package sdkx.sectiontwoproject.bean;

public class PerIn {

    /**
     * success : true
     * status : 200
     * code : 200
     * msg : 操作成功
     * data : {"serviceType":"初次申领","carType":"K1","IDCard":"370921199808013934","sex":"男","num":5,"name":"安卓测试勿动","photo":"data:image/png;bas"}
     */

    private boolean success;
    private String status;
    private String code;
    private String msg;
    private DataBean data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * serviceType : 初次申领
         * carType : K1
         * IDCard : 370921199808013934
         * sex : 男
         * num : 5
         * name : 安卓测试勿动
         * photo : data:image/png;bas
         */

        private String serviceType;
        private String carType;
        private String IDCard;
        private String sex;
        private int num;
        private String name;
        private String photo;

        public String getServiceType() {
            return serviceType;
        }

        public void setServiceType(String serviceType) {
            this.serviceType = serviceType;
        }

        public String getCarType() {
            return carType;
        }

        public void setCarType(String carType) {
            this.carType = carType;
        }

        public String getIDCard() {
            return IDCard;
        }

        public void setIDCard(String IDCard) {
            this.IDCard = IDCard;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }
    }
}
