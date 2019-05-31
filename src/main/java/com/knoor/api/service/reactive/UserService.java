package com.knoor.api.service.reactive;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.knoor.api.exception.BusinessException;
import com.knoor.api.model.db.UserModel;
import com.knoor.api.service.IDBService;
/**
 * 
 * @author ahajri
 *
 */
@Service
public class UserService implements IDBService<UserModel> {
	
	ReactiveMongoTemplate reactiveMongoTemplate;
	MongoTemplate mongoTemplate ;
	

	@Autowired
	public UserService(ReactiveMongoTemplate reactiveMongoTemplate,MongoTemplate mongoTemplate) {
		super();
		this.reactiveMongoTemplate = reactiveMongoTemplate;
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public UserModel createOrUpdate(UserModel model) throws BusinessException {
		 return mongoTemplate.save(model);
	}

	@Override
	public void delete(UserModel model) throws BusinessException {
		mongoTemplate.remove(model);
	}

	@Override
	public List<UserModel> searchByExample(UserModel example) throws BusinessException {
		Query q = new Query();
		q.addCriteria(Criteria.byExample(example));
		return mongoTemplate.find(q, UserModel.class);
	}

	@Override
	public UserModel insert(UserModel model) throws BusinessException {
		return mongoTemplate.insert(model);
	}
	
	public UserModel findByEmail(@NotNull String email) throws BusinessException{
		Query q  = new Query(Criteria.where("email").is(email));
		return mongoTemplate.findOne(q, UserModel.class);
	}

}
