package cn.com.fotic.eimp.config;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author liugj 队列配置类
 */
@Data
@Component
@ConfigurationProperties()
public class QueueProperties {

	private Map<String, QueueInfo> queue = new HashMap<>();

	@Data
	public static class QueueInfo {
		private String destination;
		private String concurrency;
	}

	/**
	 * 获取属性值
	 * 
	 * @param name
	 * @return
	 */
	public String getDestination(String name) {
		return queue.get(name).getDestination();
	}
}
