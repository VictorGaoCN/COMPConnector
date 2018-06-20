package cn.chinaunicom.open.urlconnection;

import java.io.IOException;

/**
 * @author Victor Gao
 * @createTime 2018-04-02
 * @version
 */
public interface Connection {

    /**
     * 执行请求方法
     * @exception IOException
     * @return
     */
    String doConnect() throws IOException;

}