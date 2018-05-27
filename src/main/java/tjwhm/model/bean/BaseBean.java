package tjwhm.model.bean;

public class BaseBean<T> {
    public BaseBean(int error_code, String message, T data) {
        this.error_code = error_code;
        this.message = message;
        this.data = data;
    }

    public int error_code;
    public String message;
    public T data;
}
