package edu.wayne.capstone.references.repository;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.OrderSpecifier.NullHandling;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import edu.wayne.capstone.references.data.Article;
import edu.wayne.capstone.references.data.QArticle;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@BasePathAwareController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ArticleController {

	private final ArticleRepository articleRepository;
	private final PagedResourcesAssembler pagedResourcesAssembler;
	private final PredicateService predicateService;

	@PersistenceContext
	private final EntityManager entityManager;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Transactional(readOnly = true)
	@RequestMapping(path = "articles", method = RequestMethod.GET, produces = "application/hal+json")
	@ResponseBody
	PagedResources<Article> getAll(Pageable pageable, @RequestParam(defaultValue = "false") Boolean withTotal,
			@RequestParam final MultiValueMap<String, String> parameters,
			final PersistentEntityResourceAssembler resourceAssembler) {

		// Get value of withTotal flag contained within parameters
		String withTotalString = parameters.getFirst("withTotal");
		withTotal = Boolean.valueOf(withTotalString);
		parameters.remove("withTotal");

		// Create predicate using parameters
		Predicate predicate = predicateService.getPredicateFromParameters(parameters, Article.class);
		JPAQuery query = new JPAQuery<>(entityManager).from(QArticle.article).where(predicate)
				.offset(pageable.getOffset()).limit(withTotal ? pageable.getPageSize() : pageable.getPageSize() + 1);

		Long totalMatchingElements = withTotal ? query.fetchCount() : 0;

		query = applySorting(query, pageable);
		List<Article> articles = query.fetch();

		Link nextPageLink = null;

		if (articles.size() > pageable.getPageSize()) {
			// Get page of articles with one more than requested size (i.e. if 20 requested,
			// retrieve 21)
			Page<Article> page = new PageImpl<>(articles, pageable, 0);

			// Get associated page resources and metadata; specifically, the link to the
			// next page of articles
			PagedResources resources = buildPagedResources(page, Article.class, pageable, pagedResourcesAssembler,
					resourceAssembler);

			// Save link to add back in later
			nextPageLink = resources.getNextLink();

			// Remove extra article so only the requested number of articles is returned
			articles.remove(articles.size() - 1);
		}
		Page<Article> articlePage = new PageImpl<>(articles, pageable, totalMatchingElements);
		PagedResources pageResources = buildPagedResources(articlePage, Article.class, pageable,
				pagedResourcesAssembler, resourceAssembler);

		if (nextPageLink != null)
			pageResources.add(nextPageLink);
		return pageResources;
	}

	public JPAQuery applySorting(JPAQuery query, Pageable pageable) {
		if (pageable.getSort() != null) {
			for (Sort.Order order : pageable.getSort()) {
				PathBuilder path = new PathBuilder(Article.class, "article");

				query.orderBy(new OrderSpecifier(
						order.isAscending() ? com.querydsl.core.types.Order.ASC : com.querydsl.core.types.Order.DESC,
						path.get(order.getProperty()), NullHandling.NullsLast));
			}
		}
		return query;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> PagedResources<Resource<T>> buildPagedResources(Page<T> entity, Class<T> clazz, Pageable page,
			PagedResourcesAssembler<T> pagedResourceAssembler, PersistentEntityResourceAssembler resourceAssembler) {

		PagedResources<Resource<T>> pageResources = pagedResourcesAssembler.toResource(entity, resourceAssembler);

		if (entity.getTotalElements() == 0)
			pageResources = pagedResourcesAssembler.toEmptyResource(new PageImpl<>(new ArrayList<Article>(0), page, 0),
					Article.class, null);

		return pageResources;
	}
}