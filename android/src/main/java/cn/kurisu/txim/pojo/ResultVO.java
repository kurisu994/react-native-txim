package cn.kurisu.txim.pojo;

public class ResultVO {
    private static final int SUCCEED_CODE = 1000;
    private int code;
    private String msg;

    public ResultVO() {
    }

    public ResultVO(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isSucceed() {
        return SUCCEED_CODE == this.code;
    }
}
