package org.cloudbus.cloudsim.examples.power.planetlab;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelNull;
import org.cloudbus.cloudsim.UtilizationModelPlanetLabInMemory;
import org.cloudbus.cloudsim.examples.power.Constants;

/**
 * A helper class for the running examples for the PlanetLab workload.
 * 
 * If you are using any algorithms, policies or workload included in the power package please cite
 * the following paper:
 * 
 * Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive
 * Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in
 * Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24,
 * Issue 13, Pages: 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012
 * 
 * @author Anton Beloglazov
 * @since Jan 5, 2012
 */
public class PlanetLabHelper {

	/**
	 * Get cloudlet length from source file
	 *
	 * @param inputPath The path of a PlanetLab datacenter trace.
	 */
	public static int getCloudLengthFromFile(String inputPath) throws IOException {
		int cloudletLength;
		BufferedReader input = new BufferedReader(new FileReader(inputPath));
		cloudletLength = Integer.valueOf(input.readLine()) * 100000;
		input.close();
		return cloudletLength;
	}


	/**
	 * Get cloudlet QoS from source file
	 *
	 * @param inputPath The path of a PlanetLab datacenter trace.
	 */
	public static int getCloudQoSFromFile(String inputPath) throws IOException {
		int QoS;
		String lastLine, currentLine;
		lastLine = "";
		BufferedReader input = new BufferedReader(new FileReader(inputPath));

		while ((currentLine = input.readLine()) != null) {
			lastLine = currentLine;
		}

		QoS = Integer.valueOf(lastLine);
		input.close();
		return QoS;
	}

	/**
	 * Creates the cloudlet list planet lab.
	 * 
	 * @param brokerId the broker id
	 * @param inputFolderName the input folder name
	 * @return the list
	 * @throws FileNotFoundException the file not found exception
	 */
	public static List<Cloudlet> createCloudletListPlanetLab(int brokerId, String inputFolderName)
			throws FileNotFoundException {
		List<Cloudlet> list = new ArrayList<Cloudlet>();

		long fileSize = 300;
		long outputSize = 300;
		UtilizationModel utilizationModelNull = new UtilizationModelNull();

		File inputFolder = new File(inputFolderName);
		File[] files = inputFolder.listFiles();

		for (int i = 0; i < files.length; i++) {
			Cloudlet cloudlet = null;
			try {
				cloudlet = new Cloudlet(
						i,
						getCloudLengthFromFile(files[i].getAbsolutePath()),
						Constants.CLOUDLET_PES,
						fileSize,
						outputSize,
						new UtilizationModelPlanetLabInMemory(
								files[i].getAbsolutePath(),
								Constants.SCHEDULING_INTERVAL
						),
						utilizationModelNull,
						utilizationModelNull
				);
				cloudlet.setQoS(getCloudQoSFromFile(files[i].getAbsolutePath()));
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}
			cloudlet.setUserId(brokerId);
			cloudlet.setVmId(i);
			list.add(cloudlet);
		}
		list.sort(Comparator.comparing(Cloudlet::getQoS).reversed());
		return list;
	}

}
