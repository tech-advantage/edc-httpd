/*
 * Copyright (c) 2017. All rights reserved
 */

package fr.techad.edc.httpd.search;

/**
 * Define the Documentation Item type.
 */
public enum DocumentationItemType {
    /**
     * Unknown documentation item type
     */
    UNKNOWN,
    /**
     * Chapter documentation type. This is a documentation item which can contain DOCUMENT and CONTEXTUEL documentation item.
     */
    CHAPTER,
    /**
     * Contextual documentation item type ie bricks. It can contain ARTICLE documentation type
     */
    CONTEXTUAL,
    /**
     * Document documentation item type.It can contain ARTICLE documentation type
     */
    DOCUMENT,
    /**
     * Article documentation item type. it can't contain anything.
     */
    ARTICLE;
}
