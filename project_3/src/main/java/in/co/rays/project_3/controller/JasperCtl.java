package in.co.rays.project_3.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hibernate.impl.SessionImpl;

import in.co.rays.project_3.dto.UserDTO;
import in.co.rays.project_3.util.HibDataSource;
import in.co.rays.project_3.util.JDBCDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

/**
 * Jasper functionality Controller. Performs operation for Print pdf of
 * MarksheetMeriteList
 *
 * @author Chetan Patidar
 */
@WebServlet(name = "JasperCtl", urlPatterns = { "/ctl/JasperCtl" })
public class JasperCtl extends BaseCtl {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
	    try {
	        ResourceBundle rb = ResourceBundle.getBundle("in.co.rays.project_3.bundle.system");
	        System.out.println("bundle loaded");

	        // Resolve actual file path of the JRXML file
	        String relativePath = rb.getString("jasperctl");
	        String fullPath = getServletContext().getRealPath("/" + relativePath);
	        System.out.println("Resolved JRXML Path: " + fullPath);

	        // Compile the report
	        JasperReport jasperReport = JasperCompileManager.compileReport(fullPath);
	        System.out.println("Report compiled");

	        HttpSession session = request.getSession(true);
	        UserDTO dto = (UserDTO) session.getAttribute("user");

	        Map<String, Object> map = new HashMap<>();
	        map.put("ID", 1L);

	        java.sql.Connection conn = null;
	        String Database = rb.getString("DATABASE");

	        if ("Hibernate".equalsIgnoreCase(Database)) {
	            conn = ((SessionImpl) HibDataSource.getSession()).connection();
	        } else if ("JDBC".equalsIgnoreCase(Database)) {
	            conn = JDBCDataSource.getConnection();
	        }

	        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map, conn);
	        System.out.println("Report filled");

	        byte[] pdf = JasperExportManager.exportReportToPdf(jasperPrint);
	        System.out.println("Report exported");

	        response.setContentType("application/pdf");
	        response.getOutputStream().write(pdf);
	        response.getOutputStream().flush();

	    } catch (Exception e) {
	        e.printStackTrace();
	        response.getWriter().write("Error generating Jasper Report: " + e.getMessage());
	    }
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

	}

	@Override
	protected String getView() {
		return null;
	}

}