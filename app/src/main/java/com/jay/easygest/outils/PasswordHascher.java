package com.jay.easygest.outils;

import androidx.annotation.Keep;

import com.lambdapioneer.argon2kt.Argon2Kt;
import com.lambdapioneer.argon2kt.Argon2KtResult;
import com.lambdapioneer.argon2kt.Argon2Mode;

@Keep
public class PasswordHascher {

    @Keep
    public String getHashingPass(String password, String salt){

        final Argon2Kt argon2Kt = new Argon2Kt();

        final Argon2KtResult hashResult = argon2Kt.hash(Argon2Mode.ARGON2_I, password.getBytes(), salt.getBytes());
        final String encodedOutput = hashResult.encodedOutputAsString();

        return encodedOutput;
    }

    @Keep
    public boolean verifyHashingPass(String password, String encodingPass){
        final Argon2Kt argon2Kt = new Argon2Kt();
        final boolean verificationResult = argon2Kt.verify(Argon2Mode.ARGON2_I, encodingPass, password.getBytes());

        return verificationResult;
    }


}
