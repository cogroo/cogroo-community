package br.usp.ime.cogroo.logic;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;

@Component
@ApplicationScoped
public class CogrooMemoryReport {
  
  
  private static final Logger LOG = Logger.getLogger(CogrooMemoryReport.class);
  
  public CogrooMemoryReport() {
    
    int delay = 0;
    int period = 60 * 60 * 1000; // repeat every hour
    Timer timer = new Timer();

    timer.scheduleAtFixedRate(new TimerTask() {
      public void run() {

          double free =  Runtime.getRuntime().freeMemory() / 1024d / 1024d;
          double max =  Runtime.getRuntime().maxMemory() / 1024d / 1024d;
          double total =  Runtime.getRuntime().totalMemory() / 1024d / 1024d;
          
        String data = String.format(
            "Free: %.2f; Max: %.2f; Total: %.2f", free,
            max, total);
          
          LOG.warn(data);
      }
    }, delay, period);
  }

}
