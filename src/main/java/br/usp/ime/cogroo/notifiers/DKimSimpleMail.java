package br.usp.ime.cogroo.notifiers;

import java.io.ByteArrayInputStream;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.geronimo.mail.util.StringBufferOutputStream;
import org.apache.james.jdkim.DKIMSigner;
import org.apache.log4j.Logger;

public class DKimSimpleMail extends SimpleEmail {
  
  private static final Logger LOG = Logger
      .getLogger(DKimSimpleMail.class);

  private String dkimPrivateKey;
  private String dkimHeaderTemplate;

  public void setKDimSigner(String dkimHeaderTemplate, String dkimPrivateKey) {
    this.dkimHeaderTemplate = dkimHeaderTemplate;
    this.dkimPrivateKey = dkimPrivateKey;
  }
  
  @Override
  public void buildMimeMessage() throws EmailException {
    super.buildMimeMessage();
    
    StringBuffer data = new StringBuffer();

    StringBufferOutputStream dataStream = new StringBufferOutputStream(data);

    try {
      this.message.writeTo(dataStream);
      DKIMSigner signer = new DKIMSigner(dkimHeaderTemplate,
          DKIMSigner.getPrivateKey(dkimPrivateKey));
      
      String dkimHeader = signer.sign(new ByteArrayInputStream(data.toString()
          .getBytes()));
      
      this.message.addHeaderLine(dkimHeader);
      
    } catch (Exception e) {
      LOG.error("Failed to sign message with DKim.");
    }
    
  }

}
