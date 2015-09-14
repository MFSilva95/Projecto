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

package middleHealth.org.bn.metadata;

import java.io.InputStream;
import java.io.OutputStream;

import java.lang.reflect.AnnotatedElement;

import middleHealth.org.bn.annotations.ASN1String;
import middleHealth.org.bn.coders.DecodedObject;
import middleHealth.org.bn.coders.ElementInfo;
import middleHealth.org.bn.coders.IASN1TypesDecoder;
import middleHealth.org.bn.coders.IASN1TypesEncoder;
import middleHealth.org.bn.coders.UniversalTag;

/**
 * @author jcfinley@users.sourceforge.net
 */
public class ASN1StringMetadata
    extends ASN1FieldMetadata
{
    private boolean isUCS = false;
    private int     stringType = UniversalTag.PrintableString ;
    private boolean hasDefaults = false;

    public ASN1StringMetadata() {
        hasDefaults = true;
    }

    public ASN1StringMetadata(ASN1String annotation) {
        this(annotation.name(),annotation.isUCS(),annotation.stringType());
    }

    public ASN1StringMetadata(String  name,
                              boolean isUCS,
                              int     stringType)
    {
        super(name);
        this.isUCS = isUCS;
        this.stringType = stringType;
    }

    public boolean isUCS()
    {
        return isUCS;
    }

    public int getStringType()
    {
        return stringType;
    }

    public void setParentAnnotated(AnnotatedElement parent) {
        if(parent!=null) {
            if(parent.isAnnotationPresent(ASN1String.class)) {
                ASN1String value = parent.getAnnotation(ASN1String.class);
                stringType = value.stringType();
            }
        }
    }

    public int encode(IASN1TypesEncoder encoder, Object object, OutputStream stream,
               ElementInfo elementInfo) throws Exception {
        return encoder.encodeString(object, stream, elementInfo);
    }

    public DecodedObject decode(IASN1TypesDecoder decoder, DecodedObject decodedTag, Class objectClass, ElementInfo elementInfo, InputStream stream) throws Exception {
        return decoder.decodeString(decodedTag,objectClass,elementInfo,stream);
    }
}
