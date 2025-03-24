package net.oxbeef.wake.voter.model.precinct.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.jboss.dmr.ModelNode;

import net.oxbeef.wake.voter.model.precinct.AbstractPrecinct;
import net.oxbeef.wake.voter.model.precinct.IPrecinct;
import net.oxbeef.wake.voter.model.precinct.IPrecinctSubdivision;
import net.oxbeef.wake.voter.model.precinct.PrecinctSubdivision;
import net.oxbeef.wake.voter.model.precinct.SubdivisionStreet;

public class GenericPrecinctLoader {
	public GenericPrecinctLoader() {
	}
	
	public boolean canLoad(String id) {
	    InputStream inputStream = getClass().getClassLoader().getResourceAsStream("precincts/definitions/" + id + ".json");
	    if (inputStream == null) {
	        // Handle the case where the resource is not found
	        System.err.println("Resource not found");
	        return false;
	    }
	    try {
	    	inputStream.close();
	    } catch(IOException ioe) {
	    	// ignore
	    }
	    return true;
	}

	public IPrecinct load(String id) {
	    InputStream inputStream = getClass().getClassLoader().getResourceAsStream("precincts/definitions/" + id + ".json");
	    if (inputStream == null) {
	        // Handle the case where the resource is not found
	        System.err.println("Resource not found");
	        return null;
	    }

		try {
			String content = new String(inputStream.readAllBytes());
			ModelNode node = ModelNode.fromJSONString(content);
			String name = node.get("name").asString();
			String id2 = node.get("id").asString();
			
			GenericPrecinct gp = new GenericPrecinct(node, name, id2);
			return gp;
		} catch(IOException ioe) {
			return null;
		}
	}
	
	private static class GenericPrecinct extends AbstractPrecinct {
		private ModelNode mn;
		public GenericPrecinct(ModelNode mn, String name, String id) {
			super(name, id);
			this.mn = mn;
			addSubdivisions();
			
		}

		@Override
		public void addSubdivisions() {
			if( mn == null )
				return;
			
			List<ModelNode> list = mn.get("subdivision").asList();
			Iterator<ModelNode> listIt = list.iterator();
			while(listIt.hasNext()) {
				ModelNode working = listIt.next();
				String subName = working.get("name").asString();
				IPrecinctSubdivision sd = createSubdivision(subName);
				
				List<ModelNode> streets = working.get("streets").asList();
				Iterator<ModelNode> streetIt = streets.iterator();
				while(streetIt.hasNext()) {
					ModelNode streetNode = streetIt.next();
					String name = streetNode.get("name").asString();
					int min = streetNode.get("min").asInt(Integer.MIN_VALUE);
					int max = streetNode.get("max").asInt(Integer.MAX_VALUE);
					String type = streetNode.get("type").asString();
					int t2 = typeToInt(type);
					((PrecinctSubdivision)sd).addStreet(name, min, max, t2);
				}
				addSubdivision(sd);
				
			}
		}

		private int typeToInt(String type) {
			if( type.equalsIgnoreCase("even"))
				return SubdivisionStreet.TYPE_EVEN;
			if( type.equalsIgnoreCase("odd"))
				return SubdivisionStreet.TYPE_ODD;
			return SubdivisionStreet.TYPE_ALL;
			
		}
	}
}
