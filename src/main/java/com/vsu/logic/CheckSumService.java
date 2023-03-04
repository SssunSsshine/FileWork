package com.vsu.logic;

import com.vsu.exeption.HashCodeException;
import com.vsu.info.UserInfo;
import com.vsu.ui.ConsoleUserInterface;
import org.apache.commons.codec.digest.DigestUtils;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CheckSumService {

    private static final String STR_END = "\r\n";
    private static final Logger LOGGER = Logger.getLogger(ConsoleUserInterface .class.getName());

    public void checkHashCodes(Integer hash, Map<String, UserInfo> users){
        int a = hashCode(users);
        if (!Objects.equals(hash, hashCode(users))) {
            LOGGER.log(Level.WARNING, "Something wrong with hash code");
            throw new HashCodeException("Hash codes do not match");
        }
    }
    public int hashCode(Map<String, UserInfo> users) {
        StringBuilder stringBuilder = new StringBuilder();
        for (UserInfo user: users.values()) {
            stringBuilder.append(user.toStringForFile()).append(STR_END);
        }
        return ByteBuffer.wrap(DigestUtils.sha256Hex(stringBuilder.toString()).getBytes()).getInt();
    }
}
