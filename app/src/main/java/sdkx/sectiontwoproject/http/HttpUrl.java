package sdkx.sectiontwoproject.http;

public interface HttpUrl {

    //主地址
    String BASE_URL="http://192.168.2.107:8888";


    //获取场地
    String GETFIELD_URL="/android/v1/exam/getSitePoint";


    //查询考试车辆数据
    String GETCAR_URL="/android/v1/exam/getCarData";


}
