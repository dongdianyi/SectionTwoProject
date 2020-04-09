package sdkx.sectiontwoproject.http;

public interface HttpUrl {

    //主地址
//    String BASE_URL="http://192.168.2.107:8888";
    //淄博测试地址
    String BASE_URL="http://192.168.43.10:8888";


    //获取场地
    String GETFIELD_URL="/android/v1/exam/getSitePoint";


    //查询考试车辆数据
    String GETCAR_URL="/android/v1/exam/getCarData";

    //获取考生信息
    String PREPAREEXAM_URL="/android/v1/exam/prepareExam";




    String ANDROIDID="12345678";

    //设备唯一标识
    String SERIALNUMBER = android.os.Build.SERIAL;


}
