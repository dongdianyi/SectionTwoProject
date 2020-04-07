package sdkx.sectiontwoproject.bean;

import java.util.List;

public class Filed {

    /**
     * success : true
     * status : 200
     * code : 200
     * msg : 操作成功
     * data : [{"lng":118.05629871,"lat":36.8418661,"site":null,"hypotenuse":20,"angle":null},{"lng":118.05628164,"lat":36.84186664,"site":null,"hypotenuse":21,"angle":null},{"lng":118.05628087,"lat":36.84185522,"site":null,"hypotenuse":22,"angle":null},{"lng":118.056268,"lat":36.84185316,"site":null,"hypotenuse":23,"angle":null},{"lng":118.05626748,"lat":36.84184192,"site":null,"hypotenuse":24,"angle":null},{"lng":118.05628093,"lat":36.84184199,"site":null,"hypotenuse":25,"angle":null},{"lng":118.05628131,"lat":36.8418287,"site":null,"hypotenuse":26,"angle":null},{"lng":118.056298,"lat":36.8418284,"site":null,"hypotenuse":27,"angle":null}]
     */

    private boolean success;
    private String status;
    private String code;
    private String msg;
    private List<DataBean> data;

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * lng : 118.05629871
         * lat : 36.8418661
         * site : null
         * hypotenuse : 20
         * angle : null
         */

        private double lng;
        private double lat;
        private Object site;
        private int hypotenuse;
        private Object angle;

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public Object getSite() {
            return site;
        }

        public void setSite(Object site) {
            this.site = site;
        }

        public int getHypotenuse() {
            return hypotenuse;
        }

        public void setHypotenuse(int hypotenuse) {
            this.hypotenuse = hypotenuse;
        }

        public Object getAngle() {
            return angle;
        }

        public void setAngle(Object angle) {
            this.angle = angle;
        }
    }
}
