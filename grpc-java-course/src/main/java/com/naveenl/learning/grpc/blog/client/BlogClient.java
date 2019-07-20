package com.naveenl.learning.grpc.blog.client;

import com.proto.blog.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class BlogClient {

    //
    private void doListBlog(BlogServiceGrpc.BlogServiceBlockingStub blogClient) {
        blogClient.listBlog(ListBlogRequest.newBuilder().build()).forEachRemaining(listBlogResponse -> System.out.println(listBlogResponse.toString()));

    }

    private void doDeleteBlog(BlogServiceGrpc.BlogServiceBlockingStub blogClient) {
        System.out.println("Sending Delete blog request");
        // Create a blog first and delete the same blog
        //
        Blog blog = Blog.newBuilder()
                .setAuthorId("Deleter")
                .setTitle("To Delete")
                .setContent("This blog post will be deleted later..")
                .build();

        CreateBlogResponse createResponse = blogClient.createBlog(CreateBlogRequest.newBuilder()
                .setBlog(blog)
                .build()
        );

        String blogId = createResponse.getBlog().getId();

        DeleteBlogResponse deleteResponse = blogClient.deleteBlog(DeleteBlogRequest.newBuilder().setBlogId(blogId).build());
        System.out.println(deleteResponse);

        // now try to read again, so we should get not_found exception
        //
        System.out.println("Trying to read the deleted blog");
        ReadBlogResponse readResp = blogClient.readBlog(ReadBlogRequest.newBuilder().setBlogId(blogId).build());
        System.out.println(readResp.toString());
    }

    private void doUpdtBlog(BlogServiceGrpc.BlogServiceBlockingStub blogClient) {
        // First create Blog and then update it
        //

        Blog blog = Blog.newBuilder()
                .setAuthorId("Ralf")
                .setTitle("The Schumachers")
                .setContent("Interesting topic on Formula 1 racing")
                .build();

        CreateBlogResponse response = blogClient.createBlog(CreateBlogRequest.newBuilder()
                .setBlog(blog)
                .build()
        );

        Blog updatedBlog = Blog.newBuilder()
                .setId(response.getBlog().getId())
                .setAuthorId("Ralf Schumacher")
                .setTitle("The Schumachers [updated]")
                .setContent("Interesting topic on Formula 1 racing.. Updated with more garam masala")
                .build();

        UpdateBlogResponse resp = blogClient.updateBlog(UpdateBlogRequest.newBuilder()
                .setBlog(updatedBlog)
                .build());

        System.out.println("Received Response from updated blg request");
        System.out.println(resp.toString());
    }

    private void doCreateBlog(BlogServiceGrpc.BlogServiceBlockingStub blogClient) {

        Blog blog = Blog.newBuilder()
                .setAuthorId("Jake")
                .setTitle("Some topic")
                .setContent("Very interesting second topic")
                .build();

        CreateBlogResponse response = blogClient.createBlog(CreateBlogRequest.newBuilder()
                .setBlog(blog)
                .build()
        );
    }

    private void doReadBlog(BlogServiceGrpc.BlogServiceBlockingStub blogClient) {
        // Create a blog first and read the same blog
        //
        Blog blog = Blog.newBuilder()
                .setAuthorId("Jake")
                .setTitle("Some topic")
                .setContent("Very interesting second topic")
                .build();

        CreateBlogResponse response = blogClient.createBlog(CreateBlogRequest.newBuilder()
                .setBlog(blog)
                .build()
        );

        String blogId = response.getBlog().getId();

        ReadBlogResponse readResp = blogClient.readBlog(ReadBlogRequest.newBuilder().setBlogId(blogId).build());
        System.out.println(readResp.toString());

    }

    private void doReadBlogNotFound(BlogServiceGrpc.BlogServiceBlockingStub blogClient) {
        String blogId = "5d2dfb4351911378ceaf134a";
        ReadBlogResponse readResp = blogClient.readBlog(ReadBlogRequest.newBuilder().setBlogId(blogId).build());
        System.out.println(readResp.toString());

    }

    private void run() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost",50052)
                .usePlaintext()
                .build();

        BlogServiceGrpc.BlogServiceBlockingStub blogClient = BlogServiceGrpc.newBlockingStub(channel);

//        doUpdtBlog(blogClient);

//        doDeleteBlog(blogClient);

        doListBlog(blogClient);
    }

    public static void main(String[] args) {
        System.out.println("GRPC Blog client...");
        BlogClient client = new BlogClient();
        client.run();
    }
}
