package sdkx.sectiontwoproject.model;

import android.text.TextUtils;
import android.util.Log;

import com.liqi.nohttputils.RxNoHttpUtils;
import com.liqi.nohttputils.interfa.OnDialogGetListener;
import com.liqi.nohttputils.interfa.OnIsRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import sdkx.sectiontwoproject.http.HttpUrl;
import sdkx.sectiontwoproject.model.IModelBiz;
import sdkx.sectiontwoproject.view.IView;

/**
 * 网络请求类
 */
public class NoHttpRx implements IModelBiz {
    private IView iView;

    public NoHttpRx(IView iView) {
        this.iView = iView;
    }


    //get请求
    @Override
    public void getHttp(String flag, String url, Map mapParameter, OnDialogGetListener onDialogGetListener) {
        RxNoHttpUtils.rxNohttpRequest()
                .get()
                .url(HttpUrl.BASE_URL + url)
//                .addParameter("pageNum",1)
//                .addParameter("pageSize",10)
                .addParameter(mapParameter)
//                .setOnDialogGetListener(onDialogGetListener)请求加载框
                .setQueue(false)
                //单个请求设置读取时间(单位秒，默认以全局读取超时时间。)
                // .setReadTimeout(40)
                //单个请求设置链接超时时间(单位秒，默认以全局链接超时时间。)
                // .setConnectTimeout(30)
                //单个请求设置请求失败重试计数。默认值是0,也就是说,失败后不会再次发起请求。
                //.setRetryCount(3)
                //单个请求设置缓存key
                //.setCacheKey("get请求Key")
                //单个请求设置缓存模式
                // .setCacheMode(CacheMode.REQUEST_NETWORK_FAILED_READ_CACHE)
                //设置请求bodyEntity为StringEntity，并传请求类型。
                //.requestStringEntity(Content-Type)
                //为StringEntity添加body中String值
                //.addStringEntityParameter("请求的String")
                //从bodyEntity切换到请求配置对象
                // .transitionToRequest()
                //设置请求bodyEntity为JsonObjectEntity.json格式：{"xx":"xxx","yy":"yyy"}
                // .requestJsonObjectEntity()
                //给JsonObjectEntity添加参数和值
                //.addEntityParameter("key","Valu")
                //从bodyEntity切换到请求配置对象
                // .transitionToRequest()
                //设置请求bodyEntity为JsonListEntity.json格式：[{"xx":"xxx"},{"yy":"yyy"}]
                //.requestJsonListEntity()
                //给JsonList创造对象，并传键值参数
                //.addObjectEntityParameter("key","Valu")
                //在创造对象的上添加键值参数
                //.addEntityParameter("key","Valu")
                //把创造对象刷进进JsonList里面
                //.objectBrushIntoList()
                //从bodyEntity切换到请求配置对象
                //.transitionToRequest()
                //设置请求bodyEntity为InputStreamEntity
                //.requestInputStreamEntity(Content-Type)
                //给InputStreamEntity添加输入流
                //.addEntityInputStreamParameter(InputStream)
                //从bodyEntity切换到请求配置对象
                //.transitionToRequest()
                .builder(String.class, new OnIsRequestListener<String>() {
                    @Override
                    public void onNext(String s) {
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            if (TextUtils.isEmpty(s) || s.equals("") || s.trim().equals("")) {
                                iView.fail(flag, new Throwable("亲！取得数据为空"));
                            } else if (jsonObject.getBoolean("success")) {
                                iView.toActivityData(flag, s);

                            } else {
                                iView.fail(flag, new Throwable(s));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(Throwable throwable) {
                        iView.fail(flag, throwable);
                    }
                })
                .requestRxNoHttp();
    }

    //post请求
    @Override
    public void postHttp(String flag, String url, Map mapParameter, OnDialogGetListener onDialogGetListener) {
        RxNoHttpUtils.rxNohttpRequest()
                .post()
                .url(HttpUrl.BASE_URL + url)
//                .addParameter("pageNum",1)
//                .addParameter("pageSize",10)
//                .addParameter(mapParameter)
//                .setOnDialogGetListener(onDialogGetListener)请求加载框
                .setSign(flag)
                //单个请求设置读取时间(单位秒，默认以全局读取超时时间。)
                 .setReadTimeout(15)
                //单个请求设置链接超时时间(单位秒，默认以全局链接超时时间。)
                 .setConnectTimeout(10)
                .setRetryCount(5)//重试次数
                .setAnUnknownErrorHint("POST未知错误提示")
                .builder(String.class, new OnIsRequestListener<String>() {
                    @Override
                    public void onNext(String s) {
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            if (TextUtils.isEmpty(s) || s.equals("") || s.trim().equals("")) {
                                iView.fail(flag, new Throwable("亲！取得数据为空"));
                            } else if (jsonObject.getBoolean("success")) {
                                iView.toActivityData(flag, s);
                            } else {
                                iView.fail(flag, new Throwable(s));
                            }
                            RxNoHttpUtils.cancel(flag);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        iView.fail(flag, throwable);

                    }
                })

                .requestRxNoHttp();
    }

    //post请求
    @Override
    public void postHttpJson(String flag, String url, String parameter, OnDialogGetListener onDialogGetListener) {
        RxNoHttpUtils.rxNohttpRequest()
                .post()
                .url(HttpUrl.BASE_URL + url)
//                .addParameter("pageNum",1)
//                .addParameter("pageSize",10)
//                .addParameter(mapParameter)
//                .setOnDialogGetListener(onDialogGetListener)请求加载框
                .setSign(flag)
                //单个请求设置读取时间(单位秒，默认以全局读取超时时间。)
                 .setReadTimeout(15)
                //单个请求设置链接超时时间(单位秒，默认以全局链接超时时间。)
                 .setConnectTimeout(10)
                .setRetryCount(5)//重试次数
                .setAnUnknownErrorHint("POST未知错误提示")
                //设置请求bodyEntity为StringEntity，并传请求类型。
                .requestStringEntity("application/json")
                //为StringEntity添加body中String值
                .addStringEntityParameter(parameter)
                //从bodyEntity切换到请求配置对象
                 .transitionToRequest()
                //设置请求bodyEntity为JsonObjectEntity.json格式：{"xx":"xxx","yy":"yyy"}
//                 .requestJsonObjectEntity()
                //给JsonObjectEntity添加参数和值
//                .addEntityParameter("androidId","12345678")
                //从bodyEntity切换到请求配置对象
//                 .transitionToRequest()
                .builder(String.class, new OnIsRequestListener<String>() {
                    @Override
                    public void onNext(String s) {
                        RxNoHttpUtils.cancel(flag);
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            if (TextUtils.isEmpty(s) || s.equals("") || s.trim().equals("")) {
                                iView.fail(flag, new Throwable("亲！取得数据为空"));
                            } else if (jsonObject.getBoolean("success")) {
                                iView.toActivityData(flag, s);
                            } else {
                                iView.fail(flag, new Throwable(s));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        iView.fail(flag, throwable);

                    }
                })

                .requestRxNoHttp();
    }
}
