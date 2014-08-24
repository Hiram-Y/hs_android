package com.my.android.utils;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * 命令行操作工具类
 * @author hushuai
 *
 */
public class CMDUtil {
	/** 静默安装程序指令 */
    private static  String cmd_install = "pm install -r "; 
    
    /** 静默卸载程序指令 */
    private static String cmd_uninstall = "pm uninstall ";
	

    private static final String COMMAND_SU       = "su";
    private static final String COMMAND_SH       = "sh";
    private static final String COMMAND_EXIT     = "exit\n";
    private static final String COMMAND_LINE_END = "\n";

    /**
     * 检查手机是否root
     * 
     * @return
     */
    public static boolean checkRootPermission() {
        return execCommand("echo root", true).result == 0;
    }
    
    /**
	 * 执行静默安装程序 需root
	 * @param path apk 安装包存放的路径
	 */
    public static final CMDResult execInstall(String path){
    	 return execCommand(cmd_install + path,true);
    }
    
    /**
     * 执行静默卸载程序 需root
     * @param packageName 要卸载的应用包名
     */
    public static final CMDResult execUninstall(String packageName){
    	return execCommand(cmd_uninstall + packageName,true);
    }
    
    /**
     * 执行命令
     * 
     * @param command 命令语句
     * @param isRoot 是否用root 权限执行
     * @return 执行结果 @see {@link CMDResult}
     */
    public static CMDResult execCommand(String command, boolean isRoot) {
        return execCommand(new String[] { command }, isRoot);
    }

    /**
     * 执行命令集合
     * @param commands 命令集合
     * @param isRoot 是否用 root 权限执行
     * @return 执行结果 @see {@link CMDResult}
     */
    public static CMDResult execCommand(List<String> commands, boolean isRoot) {
        return execCommand(commands == null ? null : commands.toArray(new String[] {}), isRoot);
    }

    /**
     * 执行命令数组
     * @param commands 命令数组
     * @param isRoot 是否用 root 权限执行
     * @return 执行结果 @see {@link CMDResult}
     */
    public static CMDResult execCommand(String[] commands, boolean isRoot) {
        int result = -1;
        if (commands == null || commands.length == 0) {
            return new CMDResult(result, null, null);
        }

        Process process = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = null;
        StringBuilder errorMsg = null;

        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec(isRoot ? COMMAND_SU : COMMAND_SH);
            os = new DataOutputStream(process.getOutputStream());
            for (String command : commands) {
                if (command == null) {
                    continue;
                }
                os.write(command.getBytes());
                os.writeBytes(COMMAND_LINE_END);
                os.flush();
            }
            os.writeBytes(COMMAND_EXIT);
            os.flush();

            result = process.waitFor();
            successMsg = new StringBuilder();
            errorMsg = new StringBuilder();
            successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s;
            while ((s = successResult.readLine()) != null) {
                successMsg.append(s);
            }
            while ((s = errorResult.readLine()) != null) {
                errorMsg.append(s);
            }
        } catch (IOException e) {
            LogUtil.log(e);
        } catch (Exception e) {
            LogUtil.log(e);
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (IOException e) {
                LogUtil.log(e);
            }

            if (process != null) {
                process.destroy();
            }
        }
        CMDResult cr = new CMDResult(result, successMsg == null ? null : successMsg.toString(), errorMsg == null ? null
                : errorMsg.toString());
        return cr;
    }
    /**
     * 命令执行结果类
     * @author hushuai
     *
     */
    public static class CMDResult {

        /** 命令执行结果 **/
        public int    result;
        /** 命令执行成功返回的信息 **/
        public String successMsg;
        /** 命令执行错误返回的信息 **/
        public String errorMsg;

        public CMDResult(int result){
            this.result = result;
        }

        public CMDResult(int result, String successMsg, String errorMsg){
            this.result = result;
            this.successMsg = successMsg;
            this.errorMsg = errorMsg;
        }
        
        @Override
        public String toString() {
        	return "result:" + result + "\nsuccessMsg:"+ successMsg +"\nerrorMsg:"+errorMsg;
        }
    }
}