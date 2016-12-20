package cn.edu.bjtu.weibo.service.Impl;

import cn.edu.bjtu.weibo.service.MessageToMeService;
import cn.edu.bjtu.weibo.dao.*;
import org.springframework.*;

public class MessageToMeServiceImpl implements MessageToMeService {

	// @autowired
	UserDAO userDao;
	// @autowired
	CommentDAO commentDao;
	// @autowired
	WeiboDAO weiboDao;


	@Override
	public boolean atMeInfromWeibo(String userId, String weiboId) {
		// TODO Auto-generated method stub
		
		userDao.insertWeiboAtMe(weiboDao.getOwner(weiboId), userId, weiboId);
		return true;
	}

	@Override
	public boolean atMeInfromComment(String userId, String commentId) {
		// TODO Auto-generated method stub
		
		userDao.insertCommentAtMe(userId, commentId);
		return true;
	}

	@Override
	public boolean commentMyWeiboInform(String userId, String weiboId) {
		// TODO Auto-generated method stub
		userDao.insertWeiboCommentMe(userId,weiboId);
		return true;
	}

	@Override
	public boolean commentMyCommentInform(String userId, String commentId) {
		// TODO Auto-generated method stub
		userDao.insertCommentCommentMe(userId,commentId);
		return true;
	}

	@Override
	public boolean likeMyWeiboInform(String fromUserId, String toUserId, String weiboId) {
		// TODO Auto-generated method stub
		userDao.insertWeiboLikeMe(fromUserId, toUserId, weiboId);
		return true;
	}

	@Override
	public boolean likeMyCommentInform(String fromUserId, String toUserId, String commentId) {
		// TODO Auto-generated method stub
		userDao.insertCommentLikeMe(fromUserId, toUserId, commentId);
		return true;
	}
}
