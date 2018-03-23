package net.oxbeef.wake.voter.model.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

import net.oxbeef.wake.voter.model.Residence;
import net.oxbeef.wake.voter.model.Voter;
import net.oxbeef.wake.voter.model.precinct.IPrecinct;
import net.oxbeef.wake.voter.model.precinct.IPrecinctSubdivision;

public class HTMLOutput implements IOutputFormat {
	private String template;
	
	public HTMLOutput(String templateFile) {
		try {
			template = new String(Files.readAllBytes(Paths.get(templateFile)));
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	
	@Override
	public void begin(IPrecinct p) {
		System.out.println("<html>\n<body>\n");
	}

	private void newPage() {
		System.out.println("<p style=\"page-break-before: always\">\n");
	}
	
	@Override
	public void beginSubdivision(IPrecinct p, IPrecinctSubdivision sd) {
		System.out.println("<h3>" + p.getName() + " - " + sd.getName() + "</h3>");
		newPage();
	}

	private String asString(Residence r) {
		StringBuffer sb = new StringBuffer();
		List<Voter> voters = r.getVoters();
		Iterator<Voter> it = voters.iterator();
		Voter v = null;
		sb.append("<ul>");
		while(it.hasNext()) {
			v = it.next();
			sb.append("<li>");
			sb.append(v.getName());
			sb.append("</li>\n");
		}
		sb.append("</ul>\n\n");
		return sb.toString();
	}
	
	@Override
	public void printResidence(IPrecinct p, IPrecinctSubdivision sd, Residence r) {
		
		String residents = asString(r);
		
		String t = template.replace("${ADDR}", r.getAddr());
		t = t.replace("${NUM_SUBDIV}", ""+p.getSubdivisions().length);
		t = t.replace("${PRECINCT}", p.getId());
		t = t.replace("${RESIDENTS}", residents);
		
		System.out.println(t);
		newPage();
	}

	@Override
	public void endSubdivision(IPrecinct p, IPrecinctSubdivision sd) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void end(IPrecinct p) {
		System.out.println("</body>\n</html>");
	}

}
