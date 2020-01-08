package com.argon.jconsplit;
/**
 *
 * @author Blazin64
 */
public class RunningIndicator extends Thread
{

    /**
     * @param args the command line arguments
     */
    private String textmsg = "Working";
    public void setText(String inString)
    {
        textmsg = inString;
    }
    public void run(){
        while (true){
            System.out.print(textmsg+".  \r");
            try {
            Thread.sleep(500);
            } catch (InterruptedException e){
                continue;
            } catch (Exception e){
                continue;
            }
            System.out.print(textmsg+".. \r");
            try {
            Thread.sleep(500);
            } catch (InterruptedException e){
                continue;
            } catch (Exception e){
                continue;
            }
            System.out.print(textmsg+"...\r");
            try {
            Thread.sleep(500);
            } catch (InterruptedException e){
                continue;
            } catch (Exception e){
                continue;
            }
        }
    }
}
