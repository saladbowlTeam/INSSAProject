package inssait.controller;

import java.io.IOException;
import java.util.List;

import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import inssait.model.domain.Influencers;
import inssait.model.domain.MapDetail;
import inssait.model.domain.Members;
import inssait.model.domain.SearchInfo;
import inssait.service.InssaitService;

@CrossOrigin(origins = "http://localhost:8000")
@RestController
public class InssaitController {

	@Autowired
	private InssaitService service;

	public InssaitController() {
		System.out.println("--- InssaitController ---");
	}

	// 크롤링 해서 오라클, 엘라스틱서치에 정보 저장
	@GetMapping("/getAndSave")
	public void getAndSaveData(String id, String pw, Integer loopNum, Integer targetDate) {
		try {
			service.getAndSaveData(id, pw, loopNum, targetDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 엘라스틱서치와 연동, 정보 가져오고 저장
	@GetMapping("/load")
	public SearchHit[] loadLocationKeyword() {
		SearchHit[] searchHit = null;
		try {
			searchHit = service.getLocationList();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return searchHit;
	}

	@GetMapping("/saveLocation")
	public void saveLocationData(MapDetail mapDetail) {
		try {
			service.saveLocationData(new MapDetail(mapDetail.getEsId(), mapDetail.getAddressName(),
					mapDetail.getCategoryGroupCode(), mapDetail.getCategoryGroupName(), mapDetail.getCategoryName(),
					mapDetail.getDistance(), mapDetail.getId(), mapDetail.getPhone(), mapDetail.getPlaceName(),
					mapDetail.getPlaceUrl(), mapDetail.getRoadAddressName(), mapDetail.getX(), mapDetail.getY()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@GetMapping("/getLocationInfo")
	public SearchHit[] getLocationInfo(String indexName) {
		SearchHit[] searchHit = null;
		try {
			searchHit = service.getLocationInfo(indexName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return searchHit;
	}

	@PostMapping("/saveLocationByUser")
	public void saveLocationByUser(MapDetail mapDetail, String userId) {
		try {
			service.saveLocationByUser(mapDetail, userId);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@GetMapping("/getMyPlace")
	public SearchHit[] getMyPlace(String id) {
		SearchHit[] searchHit = null;
		try {
			searchHit = service.getMyPlace(id);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return searchHit;
	}

	@GetMapping("/getMaxNum")
	public long getMaxNum() {
		long result = 0;
		try {
			result = service.getMaxNum();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	@GetMapping("/deleteLocation")
	public void deleteLocation(String id) {
		try {
			service.deleteLocation(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@GetMapping("/deleteMyPlace")
	public void deleteMyPlace(String id) {
		try {
			service.deleteMyPlace(id);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// ======================================================

	@PostMapping("/saveSearchInfo")
	public void saveSearchInfo(SearchInfo sInfo) {
		try {
			service.saveSearchInfo(sInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@GetMapping("/getSearchInfo")
	public Iterable<SearchInfo> getAllSearchInfo() {
		return service.getAllSearchInfo();
	}

	@GetMapping("/count")
	public Integer countByDateOfSearch(String DateOfSearch) {
		return service.countByDateOfSearch(DateOfSearch);
	}

	// ======================================================

	@PostMapping("/signUp")
	public ResponseEntity<Object> signUp(Members member, Model model) {
		ResponseEntity<Object> response = null;
		try {
			if (service.signUp(member)) {
				response = ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
						.header("Content-Type", "text/html; charset=UTF-8")
						.body("<script>sessionStorage.setItem('" + member.getMemberId() + "', '" + member.getMemberId()
								+ "');" + "location.href='showMarker.html';</script>");
			} else {
				response = ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
						.header("Content-Type", "text/html; charset=UTF-8")
						.body("<script>alert('정보가 유효하지 않습니다.'); history.go(-1);</script>");
			}
		} catch (Exception e) {
			System.out.println(e);
			response = ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
					.header("Content-Type", "text/html; charset=UTF-8")
					.body("<script>alert('정보가 유효하지 않습니다.'); history.go(-1);</script>");
		}
		return response;
	}

	@PostMapping("/login2")
	public ResponseEntity<Object> login(Members member) {
		ResponseEntity<Object> response = null;
		try {
			if (service.login(member) && member.getMemberId().equals("salad")) {
				response = ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
						.header("Content-Type", "text/html; charset=UTF-8")
						.body("<script>sessionStorage.setItem('" + member.getMemberId() + "', '" + member.getMemberId()
								+ "');" + "location.href='dashboard.html';</script>");
			} else if (service.login(member)) {
				response = ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
						.header("Content-Type", "text/html; charset=UTF-8")
						.body("<script>sessionStorage.setItem('" + member.getMemberId() + "', '" + member.getMemberId()
								+ "');" + "location.href='about.html';</script>");
			} else {
				response = ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
						.header("Content-Type", "text/html; charset=UTF-8")
						.body("<script>alert('로그인 정보를 확인해주세요.'); history.go(-1);</script>");
			}
		} catch (Exception e) {
			System.out.println(e);
			response = ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
					.header("Content-Type", "text/html; charset=UTF-8")
					.body("<script>alert('로그인 정보를 확인해주세요.'); history.go(-1);</script>");
		}
		return response;
	}

	// 로그아웃 로직
	@GetMapping("/logout3")
	public ResponseEntity<Object> logout() {
		return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).header("Content-Type", "text/html; charset=UTF-8")
				.body("<script>sessionStorage.clear();"
						+ "alert('로그아웃되었습니다.'); location.href='profile.html';</script>");
	}

	// 우측 중단 팔로워수 높은 순서로 탑 5명 나열하기
	@GetMapping("/getFindFollowings")
	public List<Influencers> findFollowings() {
		return service.findFollowings();
	}
}
