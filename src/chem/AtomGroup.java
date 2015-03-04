package chem;

import java.util.ArrayList;

/**
 * Different from a molecule in some cases, because it also represents partial molecules groups (?-SO2)
 * @author Oliver
 *
 */
public class AtomGroup {
	
	/* ************ What does a molecule need to do? ************
	 *  -- Compute electrophilicity and nucleophilicity of each atom, 
	 *  		-- also, direction of attack.
	 *  -- Calculate charge distributions
	 *  STRAIN:
	 *  -- Calculate angle strain on each atom
	 *   (steric/torsional need to be calculated in pairs)
	 *  
	 *  ************ Desired Usage ************************
	 *  AtomGroup pentane = AtomGroup.createXane(5); // macro for Molecule.create("CH3CH2CH2CH2CH3");
	 *  pentane.idealConfig(); // align properly w.r.t. bond length, angles
	 *  pentane.forceGroup("?-OH", 0); // make alcohol
	 *  
	 *  AtomGroup h3po4 = AtomGroup.create("H3PO4");
	 *  
	 *  
	 */
	

	//TODO Fill in these methods
	public static AtomGroup create(String str) {}
	public void idealConfig(){}
	public void forceGroup(AtomGroup m, int n) {}
	
	public void forceGroup(String str, int n) {
		forceGroup(create(str), n);
	}
	
	ArrayList<Atom> atoms = new ArrayList<Atom>(); // have associated indices
	ArrayList<ElectronCloud> bonds = new ArrayList<ElectronCloud>();
}
