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
	 *  Molecule pentane = Molecule.createXane(5); // macro for Molecule.create("CH3CH2CH2CH2CH3");
	 *  pentane.idealConfig(); // align properly w.r.t. bond length, angles
	 *  pentane.forceGroup("?-OH", 0); // make alcohol
	 *  
	 *  Molecule h3po4 = Molecule.create("H3PO4");
	 */
	
	/**
	 * Smiles
	 * @param str
	 * @return
	 */
	public static AtomGroup create(String str) {
		//H3PO --> 
	}
	
	ArrayList<Atom> atoms = new ArrayList<Atom>(); // have associated indices
	ArrayList<ElectronCloud> bonds = new ArrayList<ElectronCloud>();
}
