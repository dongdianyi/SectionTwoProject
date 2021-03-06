package sdkx.sectiontwoproject.bean;

import java.util.List;

public class Car {


    /**
     * success : true
     * status : 200
     * code : 200
     * msg : 操作成功
     * data : {"gpswidth":0.2777777777777778,"carType":"K1","gpslength":0.5,"carPoints":[{"number":"A1","lng":117.00008,"lat":36.00003,"side":65,"hypotenuse":0,"vertex":44.99999999999999},{"number":"A2","lng":117.00009111,"lat":36.00003,"side":0,"hypotenuse":-30,"vertex":44.99999999999999},{"number":"A3","lng":117.00008,"lat":36.00004,"side":0,"hypotenuse":-30,"vertex":44.99999999999999},{"number":"A4","lng":117.00007,"lat":36.00004,"side":-25,"hypotenuse":0,"vertex":44.99999999999999},{"number":"1","lng":117.00008,"lat":36.00005,"side":30,"hypotenuse":71.59,"vertex":24.78},{"number":"2","lng":117.00008,"lat":36.00003,"side":30,"hypotenuse":-71.59,"vertex":24.78},{"number":"3","lng":117.00009,"lat":36.00004,"side":-30,"hypotenuse":-39.05,"vertex":50.19},{"number":"4","lng":117.00008,"lat":36.00005,"side":-30,"hypotenuse":39.05,"vertex":50.19}],"length":0.9,"width":0.6}
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
         * gpswidth : 0.2777777777777778
         * carType : K1
         * gpslength : 0.5
         * carPoints : [{"number":"A1","lng":117.00008,"lat":36.00003,"side":65,"hypotenuse":0,"vertex":44.99999999999999},{"number":"A2","lng":117.00009111,"lat":36.00003,"side":0,"hypotenuse":-30,"vertex":44.99999999999999},{"number":"A3","lng":117.00008,"lat":36.00004,"side":0,"hypotenuse":-30,"vertex":44.99999999999999},{"number":"A4","lng":117.00007,"lat":36.00004,"side":-25,"hypotenuse":0,"vertex":44.99999999999999},{"number":"1","lng":117.00008,"lat":36.00005,"side":30,"hypotenuse":71.59,"vertex":24.78},{"number":"2","lng":117.00008,"lat":36.00003,"side":30,"hypotenuse":-71.59,"vertex":24.78},{"number":"3","lng":117.00009,"lat":36.00004,"side":-30,"hypotenuse":-39.05,"vertex":50.19},{"number":"4","lng":117.00008,"lat":36.00005,"side":-30,"hypotenuse":39.05,"vertex":50.19}]
         * length : 0.9
         * width : 0.6
         */

        private double gpswidth;
        private String carType;
        private double gpslength;
        private double length;
        private double width;
        private double outLine;
        private double pileLine;
        private double distance;
        private LocationData location;
        private FlameoutData flameout;
        private List<CarPointsBean> carPoints;

        public double getOutLine() {
            return outLine;
        }

        public void setOutLine(double outLine) {
            this.outLine = outLine;
        }

        public double getPileLine() {
            return pileLine;
        }

        public void setPileLine(double pileLine) {
            this.pileLine = pileLine;
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        public double getGpswidth() {
            return gpswidth;
        }

        public void setGpswidth(double gpswidth) {
            this.gpswidth = gpswidth;
        }

        public String getCarType() {
            if (carType == null)
                return "";

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

        public List<CarPointsBean> getCarPoints() {
            return carPoints;
        }

        public void setCarPoints(List<CarPointsBean> carPoints) {
            this.carPoints = carPoints;
        }

        public LocationData getLocation() {
            if (location==null) {
                LocationData locationData=new LocationData();
                locationData.setSerialNumber("/dev/ttyS2");
                locationData.setBaudRate("115200");
                return locationData;
            }
            return location;
        }

        public void setLocation(LocationData location) {
            this.location = location;
        }

        public FlameoutData getFlameout() {
            if (flameout==null) {
                FlameoutData flameoutData=new FlameoutData();
                flameoutData.setSerialNumber("/dev/ttyS3");
                flameoutData.setBaudRate("115200");
                return flameoutData;
            }
            return flameout;
        }

        public void setFlameout(FlameoutData flameout) {
            this.flameout = flameout;
        }

        public static class CarPointsBean {
            /**
             * number : A1
             * lng : 117.00008
             * lat : 36.00003
             * side : 65.0
             * hypotenuse : 0.0
             * vertex : 44.99999999999999
             */

            private String number;
            private double lng;
            private double lat;
            private double side;
            private double hypotenuse;
            private double vertex;

            public String getNumber() {
                return number;
            }

            public void setNumber(String number) {
                this.number = number;
            }

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

            public double getSide() {
                return side;
            }

            public void setSide(double side) {
                this.side = side;
            }

            public double getHypotenuse() {
                return hypotenuse;
            }

            public void setHypotenuse(double hypotenuse) {
                this.hypotenuse = hypotenuse;
            }

            public double getVertex() {
                return vertex;
            }

            public void setVertex(double vertex) {
                this.vertex = vertex;
            }
        }

        public static class LocationData {
            private String baudRate;
            private String serialNumber;

            public int getBaudRate() {
                if (baudRate==null) {
                    return 115200;
                }
                if (!baudRate.matches("\\d+")) {
                    return 115200;
                }
                return Integer.parseInt(baudRate);
            }

            public void setBaudRate(String baudRate) {
                this.baudRate = baudRate;
            }

            public String getSerialNumber() {
                if (serialNumber==null) {
                    return "";
                }
                return serialNumber;
            }

            public void setSerialNumber(String serialNumber) {
                this.serialNumber = serialNumber;
            }
        }

        public static class FlameoutData {
            private String baudRate;
            private String serialNumber;

            public int getBaudRate() {
                if (baudRate==null) {
                    return 115200;
                }
                if (!baudRate.matches("\\d+")) {
                    return 115200;
                }
                return Integer.parseInt(baudRate);
            }

            public void setBaudRate(String baudRate) {
                this.baudRate = baudRate;
            }

            public String getSerialNumber() {
                if (serialNumber==null) {
                    return "";
                }
                return serialNumber;
            }

            public void setSerialNumber(String serialNumber) {
                this.serialNumber = serialNumber;
            }
        }
    }
}
