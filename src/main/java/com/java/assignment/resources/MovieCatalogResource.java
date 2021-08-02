package com.java.assignment.resources;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.java.assignment.model.CatalogItem;
import com.java.assignment.model.Movie;
import com.java.assignment.model.UserRating;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private WebClient.Builder webClientBuilder;

	@GetMapping("/{userId}")
	public ResponseEntity<List<CatalogItem>> getCatalog(@PathVariable("userId") String userId)
			throws RestClientException, IOException {
		ResponseEntity<UserRating> response = null;
		ResponseEntity<List<CatalogItem>> response2 =null;

		response = restTemplate.exchange("http://localhost:9999/rating/users/" + userId, HttpMethod.GET, getHeaders(),
				UserRating.class);

		// UserRating userRating=
		// restTemplate.getForObject("http://localhost:9999/rating/users/"+userId,
		// UserRating.class);

		List<CatalogItem> catalogItemList = response.getBody().getUserRating().stream().map(rating -> {
			// Movie movie = restTemplate.getForObject("http://localhost:9898/movies/" +
			// rating.getMovieId(), Movie.class);
			ResponseEntity<Movie> response1 = null;
			try {
				response1 = restTemplate.exchange("http://localhost:9898/movies/" + rating.getMovieId(), HttpMethod.GET,
						getHeaders(), Movie.class);
			} catch (RestClientException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// for Asynchronous call use Webclient
			/*
			 * Movie movie= webClientBuilder.build() .get()
			 * .uri("http://localhost:9898/movies/" + rating.getMovieId()) .retrieve()
			 * .bodyToMono(Movie.class) .block();
			 */

			return new CatalogItem(response1.getBody().getMovieId(), "Test", rating.getRating());
		}).collect(Collectors.toList());

		 System.out.println(catalogItemList);
		
		
		
		return response2.status(HttpStatus.OK).headers(getHeaders().getHeaders()).body(catalogItemList);

		// return Collections.singletonList(new CatalogItem("Titanic", "Test", 4));

	}

	private static HttpEntity<?> getHeaders() throws IOException {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		return new HttpEntity<>(headers);

	}
}
