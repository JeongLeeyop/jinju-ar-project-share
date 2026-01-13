package com.community.cms.api.post.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.community.cms.api.board.exception.BoardNotFoundException;
import com.community.cms.api.board.repository.BoardRepository;
import com.community.cms.api.channel.repository.ChannelRepository;
import com.community.cms.api.channel.service.ChannelMemberPermissionService;
import com.community.cms.api.event.dto.EventHistoryDto;
import com.community.cms.api.event.service.EventHistoryService;
import com.community.cms.api.new_alarm.repository.NewAlarmRepository;
import com.community.cms.api.post.dto.PostDataDto;
import com.community.cms.api.post.dto.PostDto;
import com.community.cms.api.post.dto.mapper.PostMapper;
import com.community.cms.api.post.dto.search.PostSearch;
import com.community.cms.api.post.exception.PostAccessDenyException;
import com.community.cms.api.post.exception.PostNotFoundException;
import com.community.cms.api.post.exception.PostNotValidException;
import com.community.cms.api.post.exception.PostSearchNotValidExcpetion;
import com.community.cms.api.post.repository.PostLikeRepository;
import com.community.cms.api.post.repository.PostRepository;
import com.community.cms.api.post.repository.query.PostQuery;
import com.community.cms.api.user.repository.UserRepository;
import com.community.cms.api.user.service.ClientUserService;
import com.community.cms.common.code.ChannelMemberPermissionType;
import com.community.cms.common.exception.NotFoundException;
import com.community.cms.common.exception.code.NotFound;
import com.community.cms.entity.Board;
import com.community.cms.entity.BoardField;
import com.community.cms.entity.Post;
import com.community.cms.entity.PostLike;
import com.community.cms.entity2.Channel;
import com.community.cms.entity2.EventHistory;
import com.community.cms.entity2.EventType;
import com.community.cms.oauth.SinghaUser;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

public interface ClientPostService {
	Page<PostDto.ClientList> list(PostSearch search, Pageable pageable, SinghaUser authUser);
	PostDto.ClientDetail detail(String uid, SinghaUser authUser);
	PostDto.ClientDetail addViewCount(String uid, SinghaUser authUser);
	PostDto.ClientDetail add(PostDto.Add addDto, SinghaUser authUser);
	PostDto.ClientDetail update(Post post, PostDto.Update postUpdateDto, SinghaUser authUser);
	void delete(String uid, SinghaUser authUser);
}

@Slf4j
@Service
@AllArgsConstructor
class ClientPostServiceImpl implements ClientPostService {
    private final PostRepository postRepository;
    private final BoardRepository boardRepository;
    	private final ChannelRepository channelRepository;
    private final ClientUserService clientUserService;
    private final EventHistoryService eventHistoryService;
	private final UserRepository userRepository;
	private final PostLikeRepository postLikeRepository;
	private final PostQuery postQuery;
	private final NewAlarmRepository newAlarmRepository;
	private final ChannelMemberPermissionService permissionService;

	@Override
	public Page<PostDto.ClientList> list(PostSearch search, Pageable pageable, SinghaUser authUser) {
		if (!StringUtils.hasText(search.getBoardUid())) throw new PostSearchNotValidExcpetion("게시판고유값을 입력하세요.");
		Board board = boardRepository.findById(search.getBoardUid()).orElseThrow(() -> new BoardNotFoundException());
		authCheck(board, authUser, "READ");

		int page = pageable.getPageNumber();
		int size = pageable.getPageSize();
		PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Direction.DESC, "createDate"));

		if (board.isNoticeState()) {
			pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.desc("noticeState"), Sort.Order.asc("createDate")));
			search.setSort("notice");
		}
		if (board.isReplyState()) pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.desc("viewOrder"), Sort.Order.asc("createDate")));
		if (StringUtils.hasText(search.getSort()) && search.getSort().equals("hot")) {
			pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.desc("likeCount"), Sort.Order.asc("createDate")));
		}

		search.setAdminStatus(false);
		if (authUser != null) {
			if (authUser.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
				search.setAdminStatus(true);
			}
		}

		if (board.isSecretState()) {
			search.setSecretBoard(true);
			if (authUser != null) {
				search.setUserUid(authUser.getUser().getUid()); 
				if (authUser.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
					search.setSecretBoard(false);
				}
			}
		}
		List<PostDto.ClientList> list = postQuery.list(search, pageRequest, authUser);
		int count = postQuery.getTotalCount(search, pageRequest, authUser);
		return new PageImpl<>(list, pageRequest, count);
	}

	@Override
	public PostDto.ClientDetail detail(String uid, SinghaUser authUser) {
		Optional<Post> optional = postRepository.findById(uid);
		optional.orElseThrow(() -> new PostNotFoundException());
		Post post = optional.get();
		Board board = boardRepository.findById(post.getBoardUid()).orElseThrow(() -> new BoardNotFoundException());
		authCheck(board, authUser, "READ");

		if (board.isSecretState()) postAuthCheck(post, authUser);

		return PostMapper.INSTANCE.entityToClientDetail(post);
	}

	@Override
	@Transactional
	public PostDto.ClientDetail addViewCount(String uid, SinghaUser authUser) {
		Post post = postRepository.findById(uid).orElseThrow(() -> new PostNotFoundException());
		Board board = boardRepository.findById(post.getBoardUid()).orElseThrow(() -> new BoardNotFoundException());
		authCheck(board, authUser, "READ");
		if (board.isSecretState()) postAuthCheck(post, authUser);

		int viewCount = post.getViewCount();
		post.setViewCount(viewCount + 1);
		Post updatedPost = postRepository.save(post);

		PostDto.ClientDetail postDetail = PostMapper.INSTANCE.entityToClientDetail(updatedPost);
		if (authUser != null) {
			Optional<PostLike> optional = postLikeRepository.findByPostUidAndUserUid(uid, authUser.getUser().getUid());
			postDetail.setLikeStatus(optional.isPresent());
			if (updatedPost.getUserUid() != null) {
				if (updatedPost.getUserUid().equals(authUser.getUser().getUid())) postDetail.setHasAuthority(true);
				if (authUser.getAuthorities().stream().anyMatch(predicate -> predicate.getAuthority().equals("ROLE_ADMIN"))) {
					postDetail.setHasAuthority(true);
				}
			}
		}
		// 공지사항 새글인지 체크
		if(board.getUid().equals("8ed8c768-93e0-4502-a906-9c62bd44d26d")){
			newAlarmRepository.check(authUser.getUser().getUid(), uid);
		}
		
		return postDetail;
	}

	@Transactional
	@Override
	public PostDto.ClientDetail add(PostDto.Add addDto, SinghaUser authUser) {
		Post post = PostMapper.INSTANCE.addDtoToEntity(addDto);
		Board board = boardRepository.findById(post.getBoardUid()).orElseThrow(() -> new BoardNotFoundException());

		if (authUser != null) {
			post.setUserUid(authUser.getUser().getUid());
		}

		if (StringUtils.hasText(addDto.getParentUid())) {
			authCheck(board, authUser, "REPLY");
		} else {
			authCheck(board, authUser, "WRITE");
			post.setParentUid(null);
		}
		
		// ✅ 게시판 이용 권한 검증 (POST_USE 권한 필요) - 채널 게시판인 경우만
		if (StringUtils.hasText(addDto.getChannelUid()) && authUser != null) {
			log.info("📝 [커뮤니티 게시글 생성] 권한 검증 - userUid: {}, channelUid: {}", authUser.getUser().getUid(), addDto.getChannelUid());
			permissionService.validatePermission(authUser.getUser().getUid(), addDto.getChannelUid(), ChannelMemberPermissionType.POST_USE);
			log.info("✅ [커뮤니티 게시글 생성] POST_USE 권한 검증 통과 - userUid: {}", authUser.getUser().getUid());
		}
		
		// 공지사항 권한 체크: 슈퍼 관리자(ROLE_ADMIN) 또는 채널 관리자만 공지사항 생성 가능
		if (post.isNoticeStatus()) {
			boolean hasNoticePermission = false;
			
			// 슈퍼 관리자 체크
			if (authUser != null && authUser.getAuthorities().stream()
					.anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
				hasNoticePermission = true;
			}
			
			// 채널 관리자 체크
			if (!hasNoticePermission && StringUtils.hasText(post.getChannelUid())) {
				Channel channel = channelRepository.findById(post.getChannelUid())
						.orElseThrow(() -> new NotFoundException(NotFound.CHANNEL));
				if (authUser != null && channel.getUserUid().equals(authUser.getUser().getUid())) {
					hasNoticePermission = true;
				}
			}
			
			if (!hasNoticePermission) {
				throw new RuntimeException("공지사항은 관리자만 작성할 수 있습니다");
			}
		}
		
		validate(board.getFieldList(), addDto.getDataList());

		int viewOrder = 0;
		Optional<Post> optional = postRepository.findTopByOrderByViewOrderDesc();
		if (optional.isPresent()) viewOrder = optional.get().getViewOrder() + 1;
		post.setViewOrder(viewOrder);
		post.setDepth(1);

		if (StringUtils.hasText(addDto.getParentUid())) {
			postRepository.findById(addDto.getParentUid()).ifPresent(parent -> {
				post.setViewOrder(parent.getViewOrder());
				post.setDepth(parent.getDepth() + 1);
			});
		}

		if (!StringUtils.hasText(addDto.getWriter()) && authUser != null && authUser.getUser().getActualName() != null) {
			post.setWriter(authUser.getUser().getActualName());
		}
		post.setDeleteStatus(false);

		PostDto.ClientDetail dto = PostMapper.INSTANCE.entityToClientDetail(postRepository.save(post));
		if (authUser != null){
			eventHistoryService.add(new EventHistoryDto.add(EventType.POST.toString(), authUser.getUser().getUid(), addDto.getChannelUid(), null, post.getUid(), null, null));
		}
		return dto;
	}

	@Transactional
	@Override
	public PostDto.ClientDetail update(Post post, PostDto.Update postUpdateDto, SinghaUser authUser) {
		Board board = boardRepository.findById(post.getBoardUid()).orElseThrow(() -> new BoardNotFoundException());

		// ✅ 게시판 이용 권한 검증 (POST_USE 권한 필요) - 채널 게시판인 경우만
		if (post.getChannelUid() != null && authUser != null) {
			log.info("📝 [커뮤니티 게시글 수정] 권한 검증 - userUid: {}, channelUid: {}", authUser.getUser().getUid(), post.getChannelUid());
			permissionService.validatePermission(authUser.getUser().getUid(), post.getChannelUid(), ChannelMemberPermissionType.POST_USE);
			log.info("✅ [커뮤니티 게시글 수정] POST_USE 권한 검증 통과 - userUid: {}", authUser.getUser().getUid());
		}

		boolean authCheck = true;
		if (post.getChannelUid() != null) {
			Channel channel = channelRepository.findById(post.getChannelUid()).orElseThrow(() -> new NotFoundException(NotFound.CHANNEL));
			if (channel.getUserUid().equals(authUser.getUser().getUid())) {
				authCheck = false;
			}
		} 
		if (authCheck) {
			authCheck(board, authUser, "WRITE");
			postAuthCheck(post, authUser);
		}
		
		// 공지사항 권한 체크: 슈퍼 관리자(ROLE_ADMIN) 또는 채널 관리자만 공지사항 수정 가능
		if (postUpdateDto.isNoticeStatus()) {
			boolean hasNoticePermission = false;
			
			// 슈퍼 관리자 체크
			if (authUser != null && authUser.getAuthorities().stream()
					.anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
				hasNoticePermission = true;
			}
			
			// 채널 관리자 체크
			if (!hasNoticePermission && post.getChannelUid() != null) {
				Channel channel = channelRepository.findById(post.getChannelUid())
						.orElseThrow(() -> new NotFoundException(NotFound.CHANNEL));
				if (authUser != null && channel.getUserUid().equals(authUser.getUser().getUid())) {
					hasNoticePermission = true;
				}
			}
			
			if (!hasNoticePermission) {
				throw new RuntimeException("공지사항은 관리자만 수정할 수 있습니다");
			}
		}
		
		validate(board.getFieldList(), postUpdateDto.getDataList());
		// Post updatedPost = update(post, postUpdateDto);

        post = PostMapper.INSTANCE.updateDtoToEntity(postUpdateDto, post);
		post = postRepository.save(post);
		return PostMapper.INSTANCE.entityToClientDetail(post);
	}

	@Transactional
	@Override
	public void delete(String uid, SinghaUser authUser) {
		Optional<Post> optional = postRepository.findById(uid);
		optional.orElseThrow(() -> new PostNotFoundException());
		Post post = optional.get();

		Board board = boardRepository.findById(post.getBoardUid()).orElseThrow(() -> new BoardNotFoundException());
		
		// ✅ 게시판 이용 권한 검증 (POST_USE 권한 필요) - 채널 게시판인 경우만
		if (post.getChannelUid() != null && authUser != null) {
			log.info("📝 [커뮤니티 게시글 삭제] 권한 검증 - userUid: {}, channelUid: {}", authUser.getUser().getUid(), post.getChannelUid());
			permissionService.validatePermission(authUser.getUser().getUid(), post.getChannelUid(), ChannelMemberPermissionType.POST_USE);
			log.info("✅ [커뮤니티 게시글 삭제] POST_USE 권한 검증 통과 - userUid: {}", authUser.getUser().getUid());
		}
		
		boolean authCheck = true;
		if (post.getChannelUid() != null) {
			Channel channel = channelRepository.findById(post.getChannelUid()).orElseThrow(() -> new NotFoundException(NotFound.CHANNEL));
			if (authUser != null && channel.getUserUid().equals(authUser.getUser().getUid())) {
				authCheck = false;
			}
		} 
		if (authCheck) {
			authCheck(board, authUser, "DELETE");
			postAuthCheck(post, authUser);
		}
			
        /*
		post.getFileList().forEach(file -> attachedFileService.changeState(file.getFile().getUid(), false));
		postFileRepository.deleteByPostFilePkPostUid(post.getUid());
        */
		// postRepository.delete(post);

		post.setDeleteStatus(true);
		postRepository.save(post);
		
		Optional<List<Post>> postList = postRepository.findAllByParentUid(uid);
		if(postList.isPresent()){
			List<Post> itemList = postList.get();
			for (Post item  : itemList) {
				item.setDeleteStatus(true);
				postRepository.save(item);
			}
		}

	}

    public void validate(List<BoardField> boardFieldList, List<PostDataDto.Save> postDataList) {
		if (boardFieldList.size() != postDataList.size()) throw new PostNotValidException("게시글 데이터 개수가 게시판 필드 개수와 일치하지 않습니다.");
		int index = 0;
		for (BoardField boardField : boardFieldList) {
			PostDataDto.Save postData = postDataList.get(index);
			if (boardField.isRequiredState() && !StringUtils.hasText(postData.getInputValue()))
				throw new PostNotValidException(boardField.getFieldName() + "은(는) 필수 입력 항목입니다.");
			if (boardField.getInputLimit() < postData.getInputValue().length()
					&& !boardField.getFieldType().getTypeCode().equals("FILE")
					&& !boardField.getFieldType().getTypeCode().equals("PHOTO")) {
				throw new PostNotValidException(boardField.getFieldName() + "은(는) 최대 " + boardField.getInputLimit() + "자 입니다.");
			}
			index++;
		}
	}

	public void authCheck(Board board, SinghaUser authUser, String type) {
		String boardAuth = board.getAuthWrite().toString();
		switch (type) {
			case "READ":
				boardAuth = board.getAuthRead().toString();
				break;
			case "REPLY":
				boardAuth = board.getAuthReply().toString();
				break;
			default:
				break;
		}
		if (boardAuth.equals("GUEST"))
			return;
		if (boardAuth.equals("MEMBER")) {
			if (authUser == null) throw new PostAccessDenyException();
			/*
			int index = 0;
			for (BoardRole boardRole : board.getRoleList()) {
				for (GrantedAuthority role : userDetail.getAuthorities()) {
					if (role.getAuthority().equals("ROLE_ADMIN")) index++;
					if (boardRole.getAction().equals(type) && role.getAuthority().equals(boardRole.getRoleCode())) index++;
				}
			}
			if (index == 0) throw new PostAccessDenyException();
			*/
		}
		if (boardAuth.equals("MANAGER")) {
			if (authUser == null) throw new PostAccessDenyException();
			if (!authUser.getAuthorities().stream().anyMatch(predicate -> predicate.getAuthority().equals("ROLE_ADMIN"))) {
				throw new PostAccessDenyException();
			}
		}
	}

	public void postAuthCheck(Post post, SinghaUser authUser) {
		if (authUser != null) {
			boolean hasAuth = false;
			if (!authUser.getAuthorities().stream().anyMatch(predicate -> predicate.getAuthority().equals("ROLE_ADMIN"))) {
				if (post.getUserUid().equals(authUser.getUser().getUid())) {
					hasAuth = true;
				}
				if (post.getOwnerUid() != null && post.getOwnerUid().equals(authUser.getUser().getUid())) {
					hasAuth = true;
				}
			} else {
				hasAuth = true;
			}
			if (!hasAuth) throw new PostAccessDenyException();
		} else {
			throw new PostAccessDenyException();
		}
	}
}
