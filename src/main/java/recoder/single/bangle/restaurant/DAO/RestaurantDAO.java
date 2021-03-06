package recoder.single.bangle.restaurant.DAO;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import recoder.single.bangle.restaurant.DTO.RestaurantDTO;

@Repository
public class RestaurantDAO {

	@Autowired
	private SqlSessionTemplate sst;
	
	
	
	public List<RestaurantDTO> selectAll() throws Exception {
		return sst.selectList("Restaurant.selectAll");
	}
	
	public List<RestaurantDTO> selectN() throws Exception {
		return sst.selectList("Restaurant.selectN");
	}
	
	public List<RestaurantDTO> selectY() throws Exception {
		return sst.selectList("Restaurant.selectY");
	}
	
	public int insert(RestaurantDTO dto) throws Exception {
		return sst.insert("Restaurant.insert", dto);
	}
	
	public int getMaxSeq(String writer) throws Exception {
		return sst.selectOne("Restaurant.getMaxSeq", writer);
	}
	
	public RestaurantDTO selectBySeq(int seq) throws Exception {
		return sst.selectOne("Restaurant.selectBySeq", seq);
	}
	
	public int update(RestaurantDTO dto) throws Exception {
		return sst.update("Restaurant.update", dto);
	}
	
	public int approval(int seq) throws Exception {
		return sst.update("Restaurant.approval", seq);
	}
	
	public int delete(int seq) throws Exception {
		return sst.delete("Restaurant.delete", seq);
	}
}
