<template>
    <Header/>
    <el-card >
        <template #header>
            <div class="card-header">
                <span>购物车</span>
                <span style="float: right"><el-button type="danger" @click="delAll">清空</el-button></span>
            </div>
        </template>
        <el-table :data="data.records" stripe style="width: 100%" @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="55" />
            <el-table-column  width="180" >
                <template #default="scope">
                    <img    v-if="scope.row.goods.goodsPicture"
                            :src="`http://localhost:8080/${scope.row.goods.goodsPicture}`" width="100" height="100"/>
                    <img    v-else :src="defaultImg" width="80" height="80"/>
                </template>
            </el-table-column>
            <el-table-column prop="goods.goodsName" label="商品名称" width="600" />
            <el-table-column prop="store.storeName" label="门店名称" width="300"/>
            <el-table-column label="价格" width="200">
                <template #default="scope">
                    <span style="color: #f56c6c; font-weight: bold;">¥{{scope.row.goodsStore.goodsPrice.toFixed(2)}}</span>
                </template>
            </el-table-column>
            <el-table-column label="数量">
                <template #default="scope">
                    <el-input-number v-model="scope.row.amount" :min="1"  @change="handleChange(scope.row)" />
                </template>
            </el-table-column>
        </el-table>
        <template #footer>{{sum.toFixed(2)}}</template>
    </el-card>

</template>

<script setup>
    import Header from '../header/Header.vue'
    import {reactive,ref} from 'vue'
    import axios from 'axios'
    import {useRouter} from 'vue-router'
    import defaultImg from '@/assets/no.png'

    //取出sessionStorage中存入的用户对象
    const user = ref(JSON.parse(sessionStorage.getItem("user")))
    //被选中项的数组
    const multipleSelection = ref([])
    //总价
    const sum = ref(0)

    const data = reactive({
        records: [],
        form: {
            id: '',
            amount: ''
        }
    })

    const getData = ()=>{
        let path = `http://localhost:8080/carts/list/${user.value.id}`
        console.log(path)
        axios.get(path).then(res=>{
            data.records = res.data
        })
    }

    if(user.value !== null) {
        getData()
    }

    const handleChange = (row)=>{
        let path = 'http://localhost:8080/carts/update'
        axios.post(path,row).then(res=>{
            computerSum()
        })
    }

    const delAll = ()=>{
        let path = `http://localhost:8080/carts/delByUser/${user.value.id}`
        axios.get(path).then(res=>{
            if(res.data.code === 200){
                data.records = []
                sum.value = 0
            }
        })
    }


    //计算总价
    const computerSum = ()=>{
        sum.value = 0
        if(multipleSelection.value.length===0){//如果没有选择，总价是0
            sum.value = 0
        }else{
            multipleSelection.value.forEach(item=>{//如果有选择，遍历选中项，计算总价
                sum.value += item.goodsStore.goodsPrice * item.amount
            })
        }
        console.log(sum.value)
    }

    //选择框发生变化的回调函数
    const handleSelectionChange = (val) => {
        multipleSelection.value = val
        computerSum()
    }
</script>

<style scoped>

</style>