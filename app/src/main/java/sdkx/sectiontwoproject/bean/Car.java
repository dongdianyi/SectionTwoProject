package sdkx.sectiontwoproject.bean;

public class Car {

    /**
     * success : true
     * status : 200
     * code : 200
     * msg : 操作成功
     * data : {"gpswidth":0.5,"carType":"K1","gpslength":0.75,"length":1,"width":1}
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
         * gpswidth : 0.5
         * carType : K1
         * gpslength : 0.75
         * length : 1.0
         * width : 1.0
         */

        private double gpswidth;
        private String carType;
        private double gpslength;
        private double length;
        private double width;

        public double getGpswidth() {
            return gpswidth;
        }

        public void setGpswidth(double gpswidth) {
            this.gpswidth = gpswidth;
        }

        public String getCarType() {
            return carType;
        }

        public void setCarType(String carType) {
            this.carType = carType;
        }

        public double getGpslength() {
            return gpslength;
        }

        public void setGpslength(double gpslength) {
            this.gpslength = gpslength;
        }

        public double getLength() {
            return length;
        }

        public void setLength(double length) {
            this.length = length;
        }

        public double getWidth() {
            return width;
        }

        public void setWidth(double width) {
            this.width = width;
        }
    }
}
