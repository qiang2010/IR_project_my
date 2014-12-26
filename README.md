IR_project_my
============
这个项目主要是一个简单的信息检索系统，主要实现简单的体育新闻的检索

主要部分
============
  1. 数据的获取和预处理
  2. 信息检索后台处理
  3. 前台展示
  
1. 数据的获取和预处理
===============

  1. 爬虫获取新闻数据
  2. 对获取的html文件处理，抽取出新闻的title、keywords、des、body、time、hot等关键部分
  3. 分词： 停用词、新词等（搜狗语料库、停用词库等）
  4. 构建索引：分为两层的倒排索引（一个是title和dec、keywords的分词结果构建的倒排索引，一个是body构建的二级索引），
      还有一个正排索引用于存放文档的向量，用于计算文档之间的相似度，以此来对检索结果聚类


2. 信息检索后台处理
================                
  1. 倒排索引的合并
  2. 分层检索的合并
  3. 检索结果文档的排序
  4. 将检索结果返回给前台
  5. 同时还可以将检索结果聚类后返回给前台
  
  
3. 前台暂时  
===========
  1. 自动补齐功能
  2. 检索结果关键词的高亮显示
  3. 检索结果的聚类暂时
  4. 根据历史检索的推荐检索
  5. 检索结果的分页
  6. 检索异常的处理等


补充说明：
==========
