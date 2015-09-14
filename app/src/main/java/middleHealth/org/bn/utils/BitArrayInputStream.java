/*
 * Copyright 2006 Abdulla G. Abdurakhmanov (abdulla.abdurakhmanov@gmail.com).
 *
 * Licensed under the LGPL, Version 2 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/copyleft/lgpl.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * With any your questions welcome to my e-mail
 * or blog at http://abdulla-a.blogspot.com.
 */
package middleHealth.org.bn.utils;

import java.io.IOException;
import java.io.InputStream;

public class BitArrayInputStream extends InputStream {
    private InputStream byteStream;
    private int currentBit = 0, currentByte;

    public BitArrayInputStream(InputStream byteStream) {
        this.byteStream = byteStream;
    }

    public synchronized int read() throws IOException {
        if(currentBit==0) {
            return byteStream.read();
        }
        else {
            int nextByte = byteStream.read();
            int result = ((currentByte << currentBit) | (nextByte >> (8-currentBit)))&0xFF;
            currentByte = nextByte;
            return result;
        }
    }

    public synchronized int readBit() throws IOException {
        if(currentBit==0) {
            currentByte = byteStream.read();
        }
        currentBit++;
        int result = currentByte >> (8-currentBit) & 0x1;
        if (currentBit > 7)
            currentBit = 0;
        return result;
    }

    public synchronized int readBits(int nBits) throws IOException {
        int result = 0;
        for(int i=0;i<nBits && i <= 32;i++) {
            result= ((result<<1) | readBit());
        }
        return result;
    }

    public void skipUnreadedBits() {
        currentBit = 0;
    }
}
