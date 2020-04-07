package sdkx.sectiontwoproject.view;

public interface IView {
    void  toActivityData(String flag, String object);
    void  fail(String flag, Throwable t);
}
