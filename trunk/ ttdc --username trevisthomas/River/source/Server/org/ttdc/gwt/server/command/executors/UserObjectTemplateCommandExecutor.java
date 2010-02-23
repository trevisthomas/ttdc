package org.ttdc.gwt.server.command.executors;

import static org.ttdc.persistence.Persistence.commit;

import org.ttdc.gwt.client.beans.GUserObjectTemplate;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.beanconverters.FastPostBeanConverter;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.server.dao.UserObjectTemplateDao;
import org.ttdc.gwt.shared.commands.UserObjectTemplateCommand;
import org.ttdc.gwt.shared.commands.results.GenericCommandResult;
import org.ttdc.persistence.objects.UserObjectTemplate;

import static org.ttdc.persistence.Persistence.*;

public class UserObjectTemplateCommandExecutor extends CommandExecutor<GenericCommandResult<GUserObjectTemplate>>{
	@Override
	protected CommandResult execute() {
		GenericCommandResult<GUserObjectTemplate> result = null;
		try{
			beginSession();
			UserObjectTemplateCommand cmd = (UserObjectTemplateCommand)getCommand();
			if(cmd.getAction() == null)
				throw new RuntimeException("No action provided.  So, what do you want?");
			switch(cmd.getAction()){
			case CREATE:
				result = create(cmd);
				break;
			case DELETE:
				result = delete(cmd);
				break;
			case READ:
				result = read(cmd);
				break;
			case UPDATE:
				result = update(cmd);
				break;
			default:
				throw new RuntimeException("UserObjectTemplateCommandExecutor doesnt know that action");
			}
			commit();
		}
		catch(RuntimeException e){
			rollback();
			throw(e);
		}
		return result;
	}

	private GenericCommandResult<GUserObjectTemplate> update(UserObjectTemplateCommand cmd) {
		UserObjectTemplate template = UserObjectTemplateDao.load(cmd.getTemplateId());
		
		UserObjectTemplateDao dao = new UserObjectTemplateDao();
		dao.setCreatorId(getPerson().getPersonId());
		dao.setImageId(cmd.getImageId());
		dao.setTemplateName(cmd.getDisplayName());
		dao.setTemplateId(cmd.getTemplateId());
		dao.setType(cmd.getTemplateType());
		dao.setValue(cmd.getTemplateValue());
		
		template = dao.update();
		
		return convertToResult(template,"Template updated.");
	}

	private GenericCommandResult<GUserObjectTemplate> read(UserObjectTemplateCommand cmd) {
		UserObjectTemplate template = UserObjectTemplateDao.load(cmd.getTemplateId());
		return convertToResult(template,"Done.");
	}

	private GenericCommandResult<GUserObjectTemplate> create(UserObjectTemplateCommand cmd) {
		//Security check?
		UserObjectTemplateDao dao = new UserObjectTemplateDao();
		dao.setCreatorId(getPerson().getPersonId());
		dao.setImageId(cmd.getImageId());
		dao.setTemplateName(cmd.getDisplayName());
		dao.setType(cmd.getTemplateType());
		dao.setValue(cmd.getTemplateValue());
		UserObjectTemplate template = dao.create();
		return convertToResult(template,"Successfully created template.");		
	}
	
	private GenericCommandResult<GUserObjectTemplate> delete(UserObjectTemplateCommand cmd) {
		UserObjectTemplateDao.delete(cmd.getTemplateId());
		GenericCommandResult<GUserObjectTemplate> result = new GenericCommandResult<GUserObjectTemplate>(null,"Successfully deleted template.");
		return result;
	}

	/*
	 * Helper
	 */
	private GenericCommandResult<GUserObjectTemplate> convertToResult(UserObjectTemplate template, String msg) {
		GUserObjectTemplate gTemplate = FastPostBeanConverter.convertUserObjectTemplate(template);
		GenericCommandResult<GUserObjectTemplate> result = new GenericCommandResult<GUserObjectTemplate>(gTemplate,msg);
		return result;
	}
	
}	
