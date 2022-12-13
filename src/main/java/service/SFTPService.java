package service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import java.util.Vector;

public class SFTPService {

    Session session = null;
    Channel channel = null;
    ChannelSftp channelSftp = null;

    // 서버와 연결에 필요한 값들 초기화
    public void init(String host, String userName, String password, int port) {
        JSch jsch = new JSch();
        try {
            session = jsch.getSession(userName, host, port);
            session.setPassword(password);

            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();

            channel = session.openChannel("sftp");
            channel.connect();
        } catch (JSchException e) {
            e.printStackTrace();
        }
        channelSftp = (ChannelSftp) channel;
    }

    // 디렉토리 or 파일 존재 여부
    public boolean exists(String path) {
        Vector res = null;
        try {
            res = channelSftp.ls(path);
        } catch (SftpException e) {
            if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                return false;
            }
        }
        return res != null && !res.isEmpty();
    }

    // 디렉토리 생성
    public void mkdir(String dir, String mkdirName) {
        if (!this.exists(dir + "/" + mkdirName)) {
            try {
                channelSftp.cd(dir);
                channelSftp.mkdir(mkdirName);
            } catch (SftpException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 단일 파일 업로드
    public boolean upload(String dir, String filePath) {
        boolean result = true;
        FileInputStream in = null;
        try {
            File file = new File(filePath);
            String fileName = file.getName();
            //fileName = URLEncoder.encode(fileName,"EUC-KR");

            in = new FileInputStream(file);
            channelSftp.cd(dir);
            channelSftp.put(in, fileName);

        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    // 단일 파일 다운로드
    public void download(String dir, String downloadFileName, String path) {
        InputStream in = null;
        FileOutputStream out = null;
        try {
            channelSftp.cd(dir);
            in = channelSftp.get(downloadFileName);
        } catch (SftpException e) {
            e.printStackTrace();
        }

        try {
            out = new FileOutputStream(new File(path));
            int i;

            while ((i = in.read()) != -1) {
                out.write(i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    // 서버와 연결 끊음
    public void disconnection() {
        channelSftp.quit();
        session.disconnect();
    }

}
