package ir.theartofliving.honarezendegi.Model;


import com.google.gson.annotations.SerializedName;

public class Comment
{

    /**
     * id : 226
     * author_name : MarAnd
     * date : 2017-01-09T13:38:56
     * content : {"rendered":"<p>سلام ،<br />\nاین بخش فوق العاااااااااااااااااااااااااااااااااااااااااااااااااااااااااااااااااااده عالی بود .<br />\nممنون .<\/p>\n"}
     * author_avatar_urls : {"48":"http://2.gravatar.com/avatar/5fe450073aea045719c26607cb35245a?s=48&d=mm&r=g"}
     */

    private int id;
    private String author_name;
    private String date;
    private Content content;
    private AuthorAvatarUrls author_avatar_urls;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getAuthor_name()
    {
        return author_name;
    }

    public void setAuthor_name(String author_name)
    {
        this.author_name = author_name;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public Content getContent()
    {
        return content;
    }

    public void setContent(Content content)
    {
        this.content = content;
    }

    public AuthorAvatarUrls getAuthor_avatar_urls()
    {
        return author_avatar_urls;
    }

    public void setAuthor_avatar_urls(AuthorAvatarUrls author_avatar_urls)
    {
        this.author_avatar_urls = author_avatar_urls;
    }

    public static class Content
    {
        /**
         * rendered : <p>سلام ،<br />
         این بخش فوق العاااااااااااااااااااااااااااااااااااااااااااااااااااااااااااااااااااده عالی بود .<br />
         ممنون .</p>

         */

        private String rendered;

        public String getRendered()
        {
            return rendered;
        }

        public void setRendered(String rendered)
        {
            this.rendered = rendered;
        }

        public String toString()
        {
            return rendered;
        }
    }

    public static class AuthorAvatarUrls
    {
        /**
         * 48 : http://2.gravatar.com/avatar/5fe450073aea045719c26607cb35245a?s=48&d=mm&r=g
         */

        @SerializedName("48")
        private String imageUrl;

        public String getImageUrl()
        {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl)
        {
            this.imageUrl = imageUrl;
        }
    }
}
