/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prototype_interactions.reconnaissanceforme;

import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyApplicationListener;
import fr.dgac.ivy.IvyClient;
import fr.dgac.ivy.IvyException;
import fr.dgac.ivy.IvyMessageListener;
import java.awt.geom.Point2D;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bouzekel
 */
public class AgentReconnaisseurGeste {
    private Ivy bus;
    private Reconaisseur reconnaisseur_forme;
    private Dictionnaire dico = new Dictionnaire();
    private int cpt = 0;

    public AgentReconnaisseurGeste(String string, String string1, IvyApplicationListener il) throws IvyException {
        reconnaisseur_forme = new Reconaisseur(dico);
        bus = new Ivy(string, string1, il);
        bus.start("127.255.255.255:2010");
        
        bus.bindMsg("Geste coord=(.*)", new IvyMessageListener() {
            @Override
            public void receive(IvyClient ic, String[] strings) {
                
                Stroke stroke = new Stroke();
                String[] pointsxy = strings[0].split(";");
                
                System.out.println(pointsxy);
                
                for (int i = 0; i < pointsxy.length; i++) {
                    String[] xy = pointsxy[i].split(",");
                    double x = Double.valueOf(xy[0]);
                    double y = Double.valueOf(xy[1]);
                    stroke.addPoint(new Point2D.Double(x, y));
                }

                stroke.normalize();
                String reconnu = reconnaisseur_forme.reconnaitreForme(stroke);
                System.out.println(reconnu);
                try {
                    switch(reconnu) {
                        case "rectangle" : 
                            bus.sendMsg("FusionMultimodale:rectangle");
                            break;
                        case "ellipse" : 
                            bus.sendMsg("FusionMultimodale:ellipse");
                            break;
                    }
                    
                } catch (IvyException ex) {
                    Logger.getLogger(AgentReconnaisseurGeste.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        });

    }


}
