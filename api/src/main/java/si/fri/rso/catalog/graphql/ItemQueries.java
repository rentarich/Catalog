package si.fri.rso.catalog.graphql;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Name;

import si.fri.rso.catalog.models.dtos.ItemDTO;
import si.fri.rso.catalog.models.entities.Item;
import si.fri.rso.catalog.services.beans.ItemBean;
import com.kumuluz.ee.graphql.annotations.GraphQLClass;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@GraphQLApi
@ApplicationScoped
public class ItemQueries {


    @Inject
    private ItemBean itemBean;


    @Name("helloWorld")
    public String hello() {
        return "Hello world!";
    }

    @GraphQLQuery
    public List<ItemDTO> items() {

        return itemBean.getItemss();
    }

    @GraphQLQuery
    public ItemDTO getItem(@GraphQLArgument(name = "id") Integer id) {
        return itemBean.getItem(id);
    }


    @GraphQLMutation
    public ItemDTO addItem (@GraphQLArgument(name = "item") ItemDTO item) {
        return itemBean.createItem(item);
    }

    @GraphQLMutation
    public  boolean deleteItem(@GraphQLArgument(name = "id") Integer id) {
        return itemBean.deleteItem(id);
    }
}


/*
    HOW TO QUERY?

    POST localhost:5555/graphql
        {
        items{
          id
          title
          category
          description
        }
    POST localhost:5555/graphql
        mutation deleteItem{
            deleteItem(id:1)
        }

    POST localhost:5555/graphql
           mutation addItem{
            addItem(item:{title:"screw", description:"dd", category:"work"}) {
                description
                category
            }
}




}


 */
