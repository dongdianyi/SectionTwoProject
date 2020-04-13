package sdkx.sectiontwoproject.model;

import com.liqi.nohttputils.interfa.OnDialogGetListener;

import java.util.Map;

public interface IModelBiz {
     void getHttp( String flag, String url, Map mapParameter, OnDialogGetListener onDialogGetListener);
     void postHttp(String flag, String url, Map mapParameter, OnDialogGetListener onDialogGetListener);
     void postHttpJson(String flag, String url, String parameter, OnDialogGetListener onDialogGetListener);
}
