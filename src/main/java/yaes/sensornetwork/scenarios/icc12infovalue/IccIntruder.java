package yaes.sensornetwork.scenarios.icc12infovalue;

import yaes.sensornetwork.identification.IdentificationProperties;
import yaes.sensornetwork.identification.IntruderNodeType;
import yaes.world.physical.path.PPMTraversal;
import yaes.world.physical.path.ProgrammedPathMovement;

public class IccIntruder {

	public ProgrammedPathMovement ppm;
	public PPMTraversal ppmt;
	public IdentificationProperties ip;
	public String name;
	public IntruderNodeType type;

	public IccIntruder(String name, IntruderNodeType type, IdentificationProperties ip,
			ProgrammedPathMovement ppm) {
		this.name = name;
		this.type = type;
		this.ip = ip;
		this.ppm = ppm;
		ppmt = new PPMTraversal(ppm, 0);
	}

}
