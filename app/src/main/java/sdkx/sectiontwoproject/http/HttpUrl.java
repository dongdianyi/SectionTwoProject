package sdkx.sectiontwoproject.http;

public interface HttpUrl {

    //主地址
    String BASE_URL="http://192.168.2.118:8888";
    //淄博测试地址
//    String BASE_URL="http://192.168.43.195:8888";

    //webScoket地址
    String WEBSOCKET_URL="ws://192.168.2.118:8888/test/v1/websocket/";

    //获取场地
    String GETFIELD_URL="/android/v1/exam/getSitePoint";


    //查询考试车辆数据
    String GETCAR_URL="/android/v1/exam/getCarData";

    //获取考生信息
    String PREPAREEXAM_URL="/android/v1/exam/prepareExam";

    //完成考试提交数据
    String SETDATA_URL="/test/v1/coursepro/androidSetData";


//    String ANDROIDID="12345678";


    //设备唯一标识
    String ANDROIDID = android.os.Build.SERIAL;


}
