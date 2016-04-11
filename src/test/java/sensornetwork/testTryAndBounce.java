/**
 * 
 */
package sensornetwork;

import org.junit.Test;

import yaes.framework.simulation.SimulationOutput;
import yaes.sensornetwork.model.SensorNetworkWorld;
import yaes.sensornetwork.scenarios.tryandbounce.TABReasoner;
import yaes.sensornetwork.scenarios.tryandbounce.TABReportModel;
import yaes.sensornetwork.scenarios.tryandbounce.TABWorldModel;
import yaes.ui.text.TextUi;
import yaes.world.physical.location.Location;

/**
 * @author lboloni
 * 
 */
public class testTryAndBounce {

    private void
            sendTo(double currentTime, TABReasoner trFrom, TABReasoner trTo) {
        // on sender side
        TABReportModel reportModelFrom =
                (trFrom.getModel().getReportsForThreat("Threat").get(0));
        trFrom.reportSent(currentTime, reportModelFrom, trTo.getModel()
                .getMyself());
        // on receiver side
        trTo.intruderReported(currentTime,
                reportModelFrom.getObservationTime(), reportModelFrom
                        .getIntruderNode(), reportModelFrom
                        .getIntruderLocation(), reportModelFrom
                        .getInterestName(), reportModelFrom.getPerceptionId(),
                false, "A", trFrom.getModel().getMyself(), "Sink",
                reportModelFrom.getPathRecord());
        TextUi.println(trFrom.getModel());
        TextUi.println(trTo.getModel());

    }

    /**
     * Tests the TAB reasoner
     */
    @Test
    public void testScenario() {
        SimulationOutput sop = new SimulationOutput();
        SensorNetworkWorld world =
                new SensorNetworkWorld(new SimulationOutput());
        TABWorldModel lwmA = new TABWorldModel("A", world);
        TABReasoner trA = new TABReasoner(lwmA, sop);
        TABWorldModel lwmB = new TABWorldModel("B", world);
        TABReasoner trB = new TABReasoner(lwmB, sop);
        TABWorldModel lwmC = new TABWorldModel("C", world);
        TABReasoner trC = new TABReasoner(lwmC, sop);
        TABWorldModel lwmD = new TABWorldModel("D", world);
        TABReasoner trD = new TABReasoner(lwmD, sop);
        TABWorldModel lwmE = new TABWorldModel("E", world);
        TABReasoner trE = new TABReasoner(lwmE, sop);
        TABWorldModel lwmF = new TABWorldModel("F", world);
        TABReasoner trF = new TABReasoner(lwmF, sop);
        TABWorldModel lwmG = new TABWorldModel("G", world);
        TABReasoner trG = new TABReasoner(lwmG, sop);
        TABWorldModel lwmSink = new TABWorldModel("Sink", world);
        TABReasoner trSink = new TABReasoner(lwmSink, sop);

        // t = 0: threat is sighted
        double currentTime = 0;
        TextUi.printHeader("Time = " + currentTime);
        trA.intruderSighted(currentTime, "Threat", new Location(0, 0),
                "InterestName", 0);
        TextUi.println(lwmA);
        // t = 1: threat is sighted again, it supposed to oclude the old
        // sighting
        currentTime = 1;
        TextUi.printHeader("Time = " + currentTime);
        trA.intruderSighted(currentTime, "Threat", new Location(10, 10),
                "InterestName", 1);
        sendTo(currentTime, trA, trB);
        // t = 2: B sends to C
        currentTime = 2;
        TextUi.printHeader("Time = " + currentTime);
        sendTo(currentTime, trB, trC);
        // t = 3: C sends to D
        currentTime = 3;
        TextUi.printHeader("Time = " + currentTime);
        sendTo(currentTime, trC, trD);
        // t = 4: D times out
        currentTime = 4;
        TextUi.printHeader("Time = " + currentTime);
        TABReportModel reportModelC =
                (trC.getModel().getReportsForThreat("Threat").get(0));
        trC.reportTimeout(currentTime, reportModelC);
        TextUi.println(trC.getModel());
        sendTo(currentTime, trC, trE);
        // t = 5; E times out
        currentTime = 5;
        TextUi.printHeader("Time = " + currentTime);
        trC.reportTimeout(currentTime, reportModelC);
        trC.reportCanNotSend(currentTime, reportModelC);
        TextUi.println(trC.getModel());
        TABReportModel reportModelB =
                (trB.getModel().getReportsForThreat("Threat").get(0));
        trB.reportReturned(currentTime, reportModelB,
                reportModelC.getPathRecord());
        TextUi.println(trB.getModel());
        // t = 6, B sends to F
        currentTime = 6;
        TextUi.printHeader("Time = " + currentTime);
        sendTo(currentTime, trB, trF);
        // t = 7, F sends to G
        currentTime = 7;
        TextUi.printHeader("Time = " + currentTime);
        sendTo(currentTime, trF, trG);
        // t = 8, G sends to Sink
        currentTime = 8;
        TextUi.printHeader("Time = " + currentTime);
        sendTo(currentTime, trG, trSink);
    }

}
