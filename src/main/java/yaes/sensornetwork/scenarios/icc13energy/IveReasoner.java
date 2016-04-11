package yaes.sensornetwork.scenarios.icc13energy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import yaes.framework.simulation.SimulationOutput;
import yaes.sensornetwork.scenarios.icc13energy.IveReportModel.IveReportModelState;
import yaes.world.physical.location.Location;

public class IveReasoner implements Serializable, IveConstants {
    private static final long serialVersionUID = -4418678293148527641L;
    private IveWorldModel model;
    private SimulationOutput sop;

    /**
     * @param model
     * @param simulationOutput
     */
    public IveReasoner(IveWorldModel model, SimulationOutput sop) {
        this.model = model;
        this.sop = sop;
    }

    /**
     * @return the model
     */
    public IveWorldModel getModel() {
        return model;
    }

    /**
     * An intruder was reported to me
     * 
     * This shadows all the other reports
     */
    public void intruderReported(double currentTime, double observationTime,
            String intruderName, Location location, String interestName,
            int perceptionId, boolean overheardOnly, String sourceNode,
            String reportingNode, String destinationNode) {
        sop.update(InternalMetrics_Ive_CountIntruderReported, 1);
        if (!model.addIntruderAtLocation(observationTime, intruderName, location)) {
            return;
        }
        sop.update(InternalMetrics_Ive_CountIntruderReportedNewInfo, 1);
        occludeAllReportsOfThisIntruder(intruderName);
        // create the new report
        IveReportModelState state = null;
        if (overheardOnly) {
            state = IveReportModelState.OVERHEARD;
        } else {
            state = IveReportModelState.RECEIVED_WITH_OBLIGATION;
        }        
        IveReportModel report =
                new IveReportModel(state, intruderName,
                        location, observationTime, sourceNode, perceptionId);
        model.getReportModels().add(report);
    }

    /**
     * An intruder was sighted by me
     * 
     * This shadows all the other reports
     */
    public void intruderSighted(double currentTime, String intruderName,
            Location location, String interestName, int perceptionId) {
        sop.update(InternalMetrics_Ive_CountSighted, 1);
        if (!model.addIntruderAtLocation(currentTime, intruderName, location)) {
            return;
        }
        occludeAllReportsOfThisIntruder(intruderName);
        // create the new report
        IveReportModel report =
                new IveReportModel(IveReportModelState.MY_OBSERVATION, intruderName,
                        location, currentTime, model.getMyself(), perceptionId);
        model.getReportModels().add(report);
        return;
    }


    /**
     * Called before a report is sent (because the message needs to contain the
     * new path record
     * 
     * @param currentTime
     * @param reportModel
     * @param nextNode
     */
    public void reportSent(double currentTime, IveReportModel reportModel,
            String nextNode) {
        reportModel.setSentTime(currentTime);
        reportModel.setState(IveReportModelState.SENT_BY_ME);
    }

    /**
     * Clean up all the reports related to this intruder
     * 
     * @param intruderName
     */
    protected void occludeAllReportsOfThisIntruder(String intruderName) {
        List<IveReportModel> occludedReports = new ArrayList<>();
        for (IveReportModel report : model.getReportModels()) {
            if (report.getIntruderNode().equals(intruderName)) {
                sop.update(InternalMetrics_Ive_CountOcclusion, 1);
                occludedReports.add(report);
            }
        }
        model.getReportModels().removeAll(occludedReports);
    }

}
