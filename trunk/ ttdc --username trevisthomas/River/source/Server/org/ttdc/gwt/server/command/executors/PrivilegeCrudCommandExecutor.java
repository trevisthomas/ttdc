package org.ttdc.gwt.server.command.executors;

import static org.ttdc.persistence.Persistence.beginSession;
import static org.ttdc.persistence.Persistence.commit;
import static org.ttdc.persistence.Persistence.rollback;

import java.util.List;

import org.ttdc.gwt.client.beans.GPrivilege;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.beanconverters.FastPostBeanConverter;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.server.dao.PrivilegeDao;
import org.ttdc.gwt.shared.commands.PrivilegeCrudCommand;
import org.ttdc.gwt.shared.commands.results.GenericListCommandResult;
import org.ttdc.persistence.objects.Privilege;
import org.ttdc.util.ServiceException;

public class PrivilegeCrudCommandExecutor extends CommandExecutor<GenericListCommandResult<GPrivilege>>{
	@Override
	protected CommandResult execute() {
		PrivilegeCrudCommand cmd = (PrivilegeCrudCommand)getCommand();
		GenericListCommandResult<GPrivilege> result = null;
		try{
			beginSession();
			switch(cmd.getAction()){
			case DELETE:
				result = deletePrivilege(cmd);
				break;
			case CREATE:
				result = createPrivilege(cmd);
				break;
			case READ:
				result = readAllPrivileges(cmd);
				break;
			default:
				throw new RuntimeException("I cant do that action. Feel free to teach me though.");
			}
			return result;
		}
		catch(RuntimeException e){
			rollback();
			throw e;
		}
		finally{
			commit();
		}
	}

	private GenericListCommandResult<GPrivilege> readAllPrivileges(PrivilegeCrudCommand cmd) {
		List<Privilege> list = PrivilegeDao.loadAllPrivileges();
		List<GPrivilege> gList = FastPostBeanConverter.convertPrivileges(list);
		GenericListCommandResult<GPrivilege> result = new GenericListCommandResult<GPrivilege>(gList,"Success.");
		return result;
	}

	private GenericListCommandResult<GPrivilege> createPrivilege(PrivilegeCrudCommand cmd) {
		return null;
	}
	
	

	private GenericListCommandResult<GPrivilege> deletePrivilege(PrivilegeCrudCommand cmd) {
		// TODO Auto-generated method stub
		return null;
	}

}
