package org.colorcoding.ibas.bobas.diagnosis;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.organization.IOrganizationManager;
import org.colorcoding.ibas.bobas.organization.IUser;
import org.colorcoding.ibas.bobas.organization.InvalidAuthorizationException;
import org.colorcoding.ibas.bobas.organization.OrganizationFactory;

@Path("")
public class MonitorService {

	public static final String RUNTIME_INFORMATION_TAG_MEMORY = "MEMORY";
	public static final String RUNTIME_INFORMATION_MAX_MEMORY = "MAX_MEMORY";
	public static final String RUNTIME_INFORMATION_TOTAL_MEMORY = "TOTAL_MEMORY";
	public static final String RUNTIME_INFORMATION_FREE_MEMORY = "FREE_MEMORY";
	public static final String RUNTIME_INFORMATION_USED_MEMORY = "USED_MEMORY";
	public static final String RUNTIME_INFORMATION_TAG_SYSTEM = "SYSTEM";
	public static final String RUNTIME_INFORMATION_OS_SYSTEM = "OS";

	public static final long RATE_MB = 1024L * 1024L;

	/**
	 * 检查口令
	 * 
	 * @param token
	 * @throws InvalidAuthorizationException
	 */
	protected final void checkToken(String token) throws InvalidAuthorizationException {
		try {
			IOrganizationManager orgManager = OrganizationFactory.create().createManager();
			IUser user = orgManager.getUser(token);
			if (user == null) {
				// 没有用户匹配次口令
				throw new InvalidAuthorizationException(I18N.prop("msg_bobas_no_user_match_the_token"));
			}
		} catch (Exception e) {
			throw new InvalidAuthorizationException(e);
		}
	}

	protected final String computingMemory(long value) {
		return String.format("%sMB", value / RATE_MB);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("diagnosing")
	public OperationResult<Object> diagnosing(@QueryParam("token") String token) {
		OperationResult<Object> operationResult = new OperationResult<Object>();
		try {
			this.checkToken(token);
			// 获取内存信息
			Runtime runtime = Runtime.getRuntime();
			operationResult.addInformations(RUNTIME_INFORMATION_MAX_MEMORY, computingMemory(runtime.maxMemory()),
					RUNTIME_INFORMATION_TAG_MEMORY);
			operationResult.addInformations(RUNTIME_INFORMATION_TOTAL_MEMORY, computingMemory(runtime.totalMemory()),
					RUNTIME_INFORMATION_TAG_MEMORY);
			operationResult.addInformations(RUNTIME_INFORMATION_FREE_MEMORY, computingMemory(runtime.freeMemory()),
					RUNTIME_INFORMATION_TAG_MEMORY);
			operationResult.addInformations(RUNTIME_INFORMATION_USED_MEMORY,
					computingMemory(runtime.totalMemory() - runtime.freeMemory()), RUNTIME_INFORMATION_TAG_MEMORY);
			// 获取操作系统
			operationResult.addInformations(RUNTIME_INFORMATION_OS_SYSTEM, System.getProperty("os.name"),
					RUNTIME_INFORMATION_TAG_SYSTEM);
		} catch (Exception e) {
			operationResult.setError(e);
		}
		return operationResult;
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("beating")
	public String beating() {
		return "ok";
	}
}
