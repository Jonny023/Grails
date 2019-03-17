package com.base

import org.flowable.engine.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 *  flowable基础服务类
 * @Author Lee* @Description
 * @Date 2019年03月18日 0:00
 *
 */

@Service
class BaseService {

    @Autowired
    private RepositoryService repositoryService

    @Autowired
    private RuntimeService runtimeService

    @Autowired
    private TaskService taskService

    @Autowired
    private IdentityService identityService

    @Autowired
    private HistoryService historyService

    @Autowired
    private ManagementService managementService


}
